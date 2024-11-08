package ru.expensive.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.GroupSetting;
import ru.expensive.api.feature.module.setting.implement.MultiSelectSetting;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.common.util.task.TaskPriority;
import ru.expensive.core.Expensive;
import ru.expensive.implement.events.player.PostRotationMovementInputEvent;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.implement.features.commands.defaults.DebugCommand;
import ru.expensive.implement.features.modules.combat.killaura.attack.AttackHandler;
import ru.expensive.implement.features.modules.combat.killaura.attack.AttackPerpetrator;
import ru.expensive.implement.features.modules.combat.killaura.attack.ClickScheduler;
import ru.expensive.implement.features.modules.combat.killaura.attack.SprintManager;
import ru.expensive.implement.features.modules.combat.killaura.rotation.*;
import ru.expensive.implement.features.modules.combat.killaura.rotation.angle.*;
import ru.expensive.implement.features.modules.combat.killaura.target.TargetSelector;
import ru.expensive.implement.screens.menu.components.implement.window.implement.module.InfoWindow;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuraModule extends Module {
    TargetSelector targetSelector = new TargetSelector();
    PointFinder pointFinder = new PointFinder();
    ValueSetting maxDistanceSetting = new ValueSetting("Max Distance",
            "Sets the value of the maximum target search distance")
            .setValue(3.0F).range(1.0F, 6.0F);
    MultiSelectSetting targetTypeSetting = new MultiSelectSetting("Target Type",
            "Filters the entire list of targets by type")
            .value("Players", "Mobs", "Animals", "Friends");
    MultiSelectSetting attackSetting = new MultiSelectSetting("Attack setting", "Allows you to customize the attack")
            .value("Only Critical", "Raytrace check", "Dynamic Cooldown", "Break Shield", "Un Press Shield");
    SelectSetting correctionType = new SelectSetting("Correction Type", "Selects the type of correction")
            .value("Free", "Focused");
    GroupSetting correctionGroupSetting = new GroupSetting("Move correction",
            "Prevents detection by movement sensitive anticheats.")
            .settings(correctionType);
    SelectSetting sprintMode = new SelectSetting("Sprint Mode", "Allows you to select a sprint mod")
            .value("Bypass", "Default", "None");
    SelectSetting aimMode = new SelectSetting("Aim Time", "Allows you to select the timing of the rotation")
            .value("Normal", "Snap", "One Tick");
    AttackPerpetrator attackPerpetrator = new AttackPerpetrator();
    @NonFinal
    LivingEntity target = null;

    public AuraModule() {
        super("Aura", ModuleCategory.COMBAT);
        setup(maxDistanceSetting, targetTypeSetting, attackSetting, correctionGroupSetting, sprintMode, aimMode);
    }

    @Override
    public void deactivate() {
        targetSelector.releaseTarget();
        target = null;
        super.deactivate();
    }

    @EventHandler
    public void onPostRotationMovementInput(PostRotationMovementInputEvent postRotationMovementInputEvent) {
        target = updateTarget();
        if (target != null) {
            RotationController rotationController = RotationController.INSTANCE;
            Vec3d attackVector = pointFinder.computeVector(target, maxDistanceSetting.getValue(),
                    rotationController.getRotation(),
                    getSmoothMode().randomValue());
            Angle angle = AngleUtil.fromVec3d(attackVector.subtract(mc.player.getEyePos()));
            rotateToTarget(target, new Angle.VecRotation(angle, attackVector), rotationController);
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (target != null) {
            attackTarget(target, RotationController.INSTANCE.getCurrentAngle());
            TargetSelector.EntityFilter filter = new TargetSelector.EntityFilter(targetTypeSetting.getSelected());

            targetSelector.searchTargets(mc.world.getEntities(), maxDistanceSetting.getValue());
            targetSelector.validateTarget(filter::isValid);

        }
    }

    private LivingEntity updateTarget() {
        TargetSelector.EntityFilter filter = new TargetSelector.EntityFilter(targetTypeSetting.getSelected());

        targetSelector.searchTargets(mc.world.getEntities(), maxDistanceSetting.getValue());
        targetSelector.validateTarget(filter::isValid);

        return targetSelector.getCurrentTarget();
    }

    private void attackTarget(LivingEntity target, Angle angle) {
        AttackPerpetrator attackPerpetrator = Expensive.getInstance().getAttackPerpetrator();

        AttackPerpetrator.AttackPerpetratorConfigurable configurable = new AttackPerpetrator.AttackPerpetratorConfigurable(
                target,
                RotationController.INSTANCE.getServerAngle(),
                maxDistanceSetting.getValue(),
                attackSetting.getSelected(),
                getSprintMode());
        if (angle != null && aimMode.isSelected("One Tick")) {
            mc.player.networkHandler
                    .sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                            angle.getYaw(), angle.getPitch(), mc.player.isOnGround()));
        }

        attackPerpetrator.performAttack(configurable);

        if (angle != null && aimMode.isSelected("One Tick")) {
            mc.player.networkHandler
                    .sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                            mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
        }
    }

    private void rotateToTarget(LivingEntity target, Angle.VecRotation rotation,
                                RotationController rotationController) {
        RotationConfig configurable = new RotationConfig(getSmoothMode(),
                DebugCommand.debug,
                correctionGroupSetting.isValue(),
                ((SelectSetting) correctionGroupSetting.getSubSetting("Correction Type")).isSelected("Free"));

        AttackHandler attackHandler = Expensive.getInstance().getAttackPerpetrator().getAttackHandler();
        ClickScheduler clickScheduler = attackHandler.getClickScheduler();

        if (aimMode.isSelected("Snap") && clickScheduler.hasTicksElapsedSinceLastClick(2)) {
            return;
        }

        if (aimMode.isSelected("One Tick")) {
            return;
        }

        rotationController.rotateTo(rotation, target, configurable, TaskPriority.HIGH_IMPORTANCE_1, this);
    }

    public SprintManager.Mode getSprintMode() {
        switch (sprintMode.getSelected()) {
            case "Bypass" -> {
                return SprintManager.Mode.BYPASS;
            }
            case "Default" -> {
                return SprintManager.Mode.DEFAULT;
            }
        }
        return SprintManager.Mode.NONE;
    }

    public AngleSmoothMode getSmoothMode() {
        if (!aimMode.isSelected("Snap")) {
            switch (InfoWindow.selectSetting.getSelected()) {
                case "FunTime" -> {
                    return new FunTimeSmoothMode();
                }
                case "ReallyWorld" -> {
                    return new ReallyWorldSmoothMode();
                }
                case "HolyWorld Classic" -> {
                    return new HolyWorldClassicSmoothMode();
                }
                case "HolyWorld Lite" -> {
                    return new HolyWorldLiteSmoothMode();
                }
            }
        } else {
            return new LinearSmoothMode();
        }
        return new ReallyWorldSmoothMode();
    }
}