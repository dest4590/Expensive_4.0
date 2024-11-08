package ru.expensive.implement.features.modules.player;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.common.util.math.Counter;
import ru.expensive.common.util.player.PlayerInventoryUtil;
import ru.expensive.common.util.task.TaskPriority;
import ru.expensive.implement.events.keyboard.KeyEvent;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationConfig;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;

@FieldDefaults(level = AccessLevel.PRIVATE)
@SuppressWarnings("all")
public class ClickPearlModule extends Module {
    public Action action = Action.START;
    Counter counter = Counter.create();
    Runnable keyPressedAction;

    public ClickPearlModule() {
        super("ClickPearl", ModuleCategory.PLAYER);
    }

    @EventHandler
    public void onKey(KeyEvent keyEvent) {
        if (keyEvent.isKeyDown(GLFW.GLFW_KEY_G) && canPerformAction()) {
            action = Action.START;
            keyPressedAction = () -> performAction();
            counter.resetCounter();
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (keyPressedAction != null) {
            keyPressedAction.run();
        }
    }

    private boolean canPerformAction() {
        return keyPressedAction == null && !mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL);
    }

    private void startAction() {
        action = Action.START;
        keyPressedAction = () -> performAction();
        counter.resetCounter();
    }

    private void performAction() {
        RotationController rotationController = RotationController.INSTANCE;

        Integer slot = PlayerInventoryUtil.INSTANCE.findHotbarSlot(Items.ENDER_PEARL);
        Hand hand = mc.player.getOffHandStack().getItem() instanceof EnderPearlItem ? Hand.OFF_HAND : Hand.MAIN_HAND;

        if (slot != null) {
            if (rotationController.getCurrentRotationPlan() != null) {
                interactWithRotation(slot, hand, rotationController);
            } else {
                interact(slot, hand);
            }
        } else {
            keyPressedAction = null;
        }
    }

    private void interactWithRotation(Integer slot, Hand hand, RotationController rotationController) {
        if (action == Action.START) {
            rotationController.rotateTo(new Angle(mc.player.getYaw(), mc.player.getPitch()),
                    RotationConfig.DEFAULT,
                    TaskPriority.HIGH_IMPORTANCE_2, this);
            switchSlotIfNeeded(slot, slot, hand);
            action = Action.WAIT;
        } else if (action == Action.WAIT && counter.isReached(70L)) {
            action = Action.USE_ITEM;
        } else if (action == Action.USE_ITEM) {
            interactWithItem(hand);
            switchSlotIfNeeded(mc.player.getInventory().selectedSlot, slot, hand);
            keyPressedAction = null;
        }
    }

    private void interact(Integer slot, Hand hand) {
        if (action == Action.START) {
            switchSlotIfNeeded(slot, slot, hand);
            action = Action.WAIT;
        } else if (action == Action.WAIT && counter.isReached(50L)) {
            action = Action.USE_ITEM;
        } else if (action == Action.USE_ITEM) {
            interactWithItem(hand);
            switchSlotIfNeeded(mc.player.getInventory().selectedSlot, slot, hand);
            keyPressedAction = null;
        }
    }

    private void switchSlotIfNeeded(Integer swapSlot, Integer slot, Hand hand) {
        if (slot != mc.player.getInventory().selectedSlot && hand != Hand.OFF_HAND) {
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(swapSlot));
        }
    }

    private void interactWithItem(Hand hand) {
        mc.interactionManager.sendSequencedPacket(mc.world,
                sequence -> new PlayerInteractItemC2SPacket(hand, sequence));
        mc.player.swingHand(hand);
    }

    public enum Action {
        START, WAIT, USE_ITEM
    }
}