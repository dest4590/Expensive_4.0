package ru.expensive.implement.features.modules.combat.killaura.rotation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import ru.expensive.common.QuickImports;
import ru.expensive.implement.features.modules.combat.killaura.rotation.angle.AngleSmoothMode;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RotationPlan implements QuickImports {
    Angle angle;
    Vec3d vec3d;
    Entity entity;
    AngleSmoothMode angleSmooth;
    int ticksUntilReset;
    float resetThreshold;
    boolean changeLook, moveCorrection, freeCorrection;

    public Angle nextRotation(Angle fromAngle, boolean isResetting) {
        if (isResetting) {
            return angleSmooth.limitAngleChange(fromAngle, AngleUtil.fromVec2f(mc.player.getRotationClient()));
        }
        return angleSmooth.limitAngleChange(fromAngle, angle, vec3d, entity);
    }
}