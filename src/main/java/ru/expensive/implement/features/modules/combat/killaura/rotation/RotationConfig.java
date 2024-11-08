package ru.expensive.implement.features.modules.combat.killaura.rotation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import ru.expensive.implement.features.modules.combat.killaura.rotation.angle.AngleSmoothMode;
import ru.expensive.implement.features.modules.combat.killaura.rotation.angle.LinearSmoothMode;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RotationConfig {
    public static RotationConfig DEFAULT = new RotationConfig(new LinearSmoothMode(),
            false, true, true);
    AngleSmoothMode angleSmooth;
    float resetThreshold = 2f;
    int ticksUntilReset = 5;
    boolean changeView, moveCorrection, freeCorrection;

    public RotationConfig(boolean changeView, boolean moveCorrection, boolean freeCorrection) {
        this(new LinearSmoothMode(), changeView, moveCorrection, freeCorrection);
    }

    public RotationConfig(boolean changeView, boolean moveCorrection) {
        this(new LinearSmoothMode(), changeView, moveCorrection, true);
    }

    public RotationConfig(boolean changeView) {
        this(new LinearSmoothMode(), changeView, true, true);
    }

    public RotationConfig(AngleSmoothMode angleSmooth, boolean changeView, boolean moveCorrection,
                          boolean freeCorrection) {
        this.angleSmooth = angleSmooth;
        this.changeView = changeView;
        this.moveCorrection = moveCorrection;
        this.freeCorrection = freeCorrection;
    }

    public RotationPlan createRotationPlan(Angle angle, Vec3d vec, Entity entity) {
        return new RotationPlan(angle, vec, entity, angleSmooth, ticksUntilReset, resetThreshold, changeView,
                moveCorrection, freeCorrection);
    }

    public RotationPlan createRotationPlan(Angle angle) {
        return new RotationPlan(angle, null, null, angleSmooth, ticksUntilReset, resetThreshold, changeView,
                moveCorrection, freeCorrection);
    }

    public RotationPlan createRotationPlan(Angle angle, Vec3d vec, Entity entity, boolean changeLook,
                                           boolean moveCorrection, boolean freeCorrection) {
        return new RotationPlan(angle, vec, entity, angleSmooth, ticksUntilReset, resetThreshold, changeLook,
                moveCorrection, freeCorrection);
    }
}