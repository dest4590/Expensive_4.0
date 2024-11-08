package ru.expensive.implement.features.modules.combat.killaura.rotation.angle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.AngleUtil;

public class HolyWorldClassicSmoothMode extends AngleSmoothMode {
    public HolyWorldClassicSmoothMode() {
        super("HolyWorld Classic");
    }

    @Override
    public Angle limitAngleChange(Angle currentAngle, Angle targetAngle, Vec3d vec3d, Entity entity) {
        Angle angleDelta = AngleUtil.calculateDelta(currentAngle, targetAngle);

        float yawDelta = angleDelta.getYaw();
        float pitchDelta = angleDelta.getPitch();

        float rotationDifference = (float) Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));

        float straightLineYaw = (float) (Math.abs(yawDelta / rotationDifference) * MathUtil.getRandom(60.0F, 90.0F));
        float straightLinePitch = (float) (Math.abs(pitchDelta / rotationDifference)
                * MathUtil.getRandom(20.0F, 25.0F));

        float moveYaw = MathHelper.clamp(yawDelta, -straightLineYaw, straightLineYaw);
        float movePitch = MathHelper.clamp(pitchDelta, -straightLinePitch, straightLinePitch);

        if (mc.player.isFallFlying()) {
            return new Angle(currentAngle.getYaw() + yawDelta, currentAngle.getPitch() + pitchDelta);
        }

        Angle moveAngle = new Angle(currentAngle.getYaw(), currentAngle.getPitch());
        moveAngle.setYaw((float) MathHelper.lerp(0.9F + MathUtil.getRandom(0, 1) * 0.7F, currentAngle.getYaw(),
                currentAngle.getYaw() + moveYaw));
        moveAngle.setPitch((float) MathHelper.lerp(0.55F + MathUtil.getRandom(0, 1) * 0.7F, currentAngle.getPitch(),
                currentAngle.getPitch() + movePitch));

        return new Angle(moveAngle.getYaw(), moveAngle.getPitch());
    }

    @Override
    public Vec3d randomValue() {
        return new Vec3d(0.04888858F, 0.06F, 0.04888858F);
    }
}
