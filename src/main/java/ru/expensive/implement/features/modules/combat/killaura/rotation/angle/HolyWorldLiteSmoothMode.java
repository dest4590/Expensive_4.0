package ru.expensive.implement.features.modules.combat.killaura.rotation.angle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.AngleUtil;

public class HolyWorldLiteSmoothMode extends AngleSmoothMode {
    public HolyWorldLiteSmoothMode() {
        super("HolyWorld Lite");
    }

    @Override
    public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
        Angle angleDelta = AngleUtil.calculateDelta(currentAngle, targetAngle);

        float yawDelta = angleDelta.getYaw();
        float pitchDelta = angleDelta.getPitch();

        float rotationDifference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));

        float straightLineYaw = Math.abs(yawDelta / rotationDifference) * 60;
        float straightLinePitch = Math.abs(pitchDelta / rotationDifference) * 45;

        float moveYaw = MathHelper.clamp(yawDelta, -straightLineYaw, straightLineYaw);
        float movePitch = MathHelper.clamp(pitchDelta, -straightLinePitch, straightLinePitch);

        Angle moveAngle = new Angle(currentAngle.getYaw(), currentAngle.getPitch());
        moveAngle.setYaw((float) MathHelper.lerp(0.9F + MathUtil.getRandom(0, 1) * 0.7F, currentAngle.getYaw(),
                currentAngle.getYaw() + moveYaw));
        moveAngle.setPitch((float) MathHelper.lerp(0.3F + MathUtil.getRandom(0, 1) * 0.7F, currentAngle.getPitch(),
                currentAngle.getPitch() + movePitch));

        return new Angle(currentAngle.getYaw() + moveYaw, currentAngle.getPitch() + movePitch);
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.7F, 0.001F, 0.7F);
    }
}
