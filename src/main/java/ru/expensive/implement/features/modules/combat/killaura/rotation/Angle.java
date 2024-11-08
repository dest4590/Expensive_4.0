package ru.expensive.implement.features.modules.combat.killaura.rotation;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.expensive.common.util.math.MathUtil;

@Getter
@Setter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Angle {
    public static Angle DEFAULT = new Angle(0, 0);
    float yaw, pitch;

    public Angle adjustSensitivity() {
        double gcd = MathUtil.computeGcd();

        Angle previousAngle = RotationController.INSTANCE.getServerAngle();

        float adjustedYaw = adjustAxis(yaw, previousAngle.yaw, gcd);
        float adjustedPitch = adjustAxis(pitch, previousAngle.pitch, gcd);

        return new Angle(adjustedYaw, MathHelper.clamp(adjustedPitch, -90f, 90f));
    }

    private float adjustAxis(float axisValue, float previousValue, double gcd) {
        float delta = axisValue - previousValue;
        return previousValue + Math.round(delta / gcd) * (float) gcd;
    }

    public final Vec3d toVector() {
        float f = pitch * 0.017453292F;
        float g = -yaw * 0.017453292F;
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    @ToString
    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class VecRotation {
        final Angle angle;
        final Vec3d vec;
    }
}
