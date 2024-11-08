package ru.expensive.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.entity.effect.StatusEffects;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.implement.events.player.KeepSprintEvent;
import ru.expensive.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoSprintModule extends Module {
    BooleanSetting keepSprintSetting = new BooleanSetting("Keep Sprint",
            "Keep sprint before impact, thus not slowing you down")
            .setValue(true);
    @Setter
    @NonFinal
    boolean emergencyStop = false;

    public AutoSprintModule() {
        super("AutoSprint", "Auto Sprint", ModuleCategory.MOVEMENT);
        setup(keepSprintSetting);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        boolean horizontalCollision = mc.player.horizontalCollision && !mc.player.collidedSoftly;
        if (canStartSprinting() && !horizontalCollision && !mc.options.sprintKey.isPressed()) {
            mc.player.setSprinting(true);
        }
        if (mc.player.isSprinting() && (!canSprint() || emergencyStop)) {
            mc.player.setSprinting(false);
        }
        emergencyStop = false;
    }

    @EventHandler
    public void onKeepSprint(KeepSprintEvent keepSprintEvent) {
        if (keepSprintSetting.isValue()) {
            float multiplier = 1.0F;
            mc.player.setVelocity(mc.player.getVelocity().x / 0.6 * multiplier, mc.player.getVelocity().y,
                    mc.player.getVelocity().z / 0.6 * multiplier);
            mc.player.setSprinting(true);
        }
    }

    @Override
    public void deactivate() {
        if (mc.player.isSprinting())
            mc.player.setSprinting(false);
        super.deactivate();
    }

    private boolean canStartSprinting() {
        boolean hasBlindness = mc.player.hasStatusEffect(StatusEffects.BLINDNESS);
        return !mc.player.isSprinting() && isWalking() && !hasBlindness && !mc.player.isFallFlying();
    }

    private boolean canSprint() {
        int foodLevel = mc.player.getHungerManager().getFoodLevel();
        return mc.player.hasVehicle() || (float) foodLevel > 6.0F || mc.player.getAbilities().allowFlying;
    }

    private boolean isWalking() {
        return mc.player.isSubmergedInWater() ? mc.player.input.hasForwardMovement()
                : (double) mc.player.input.movementForward > 0;
    }
}
