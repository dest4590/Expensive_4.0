package ru.expensive.implement.features.modules.combat.killaura.rotation.angle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.AngleUtil;

import java.util.Objects;

public class FunTimeSmoothMode extends AngleSmoothMode {
    public FunTimeSmoothMode() {
        super("FunTime");
    }

    @Override
    public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
        Angle angleDelta = AngleUtil.calculateDelta(currentAngle, targetAngle);

        float yawDelta = angleDelta.getYaw();
        float pitchDelta = angleDelta.getPitch();

        float rotationDifference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));

        float distanceFactor = Math.max(Math.min(Objects.requireNonNull(mc.player).distanceTo(entity) / 2, 1.0F), 0.1F);

        float lineYaw = (Math.abs(yawDelta / rotationDifference) * 90) * distanceFactor;
        float linePitch = (Math.abs(pitchDelta / rotationDifference) * 90) * distanceFactor;

        float moveYaw = MathHelper.clamp(yawDelta, -lineYaw, lineYaw);
        float movePitch = MathHelper.clamp(pitchDelta, -linePitch, linePitch);

        Angle moveAngle = new Angle(currentAngle.getYaw(), currentAngle.getPitch());
        moveAngle.setYaw((float) MathHelper.lerp(0.7F + MathUtil.getRandom(0, 1) * 0.7F, currentAngle.getYaw(),
                currentAngle.getYaw() + moveYaw));
        moveAngle.setPitch((float) MathHelper.lerp(0.7F + MathUtil.getRandom(0, 1) * 0.7F, currentAngle.getPitch(),
                currentAngle.getPitch() + movePitch));

        return new Angle(moveAngle.getYaw(), moveAngle.getPitch());
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.06F, 0.02F, 0.06F);
    }
}
