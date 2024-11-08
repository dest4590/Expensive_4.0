package ru.expensive.common.util.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.KeyBinding;
import ru.expensive.common.QuickImports;

import java.util.Objects;
import java.util.stream.Stream;

public class MovingUtil implements QuickImports {
    public static KeyBinding[] getMovementKeys(boolean includeSneak) {
        return Stream.of(
                        mc.options.forwardKey,
                        mc.options.backKey,
                        mc.options.leftKey,
                        mc.options.rightKey,
                        mc.options.jumpKey,
                        mc.options.sprintKey,
                        includeSneak ? mc.options.sneakKey : null).filter(Objects::nonNull)
                .toArray(KeyBinding[]::new);
    }

    public static boolean hasPlayerMovement() {
        return mc.player.input.movementForward != 0f || mc.player.input.movementSideways != 0f;
    }

    public static double[] calculateDirection(final double distance) {
        float forward = mc.player.input.movementForward;
        float sideways = mc.player.input.movementSideways;
        float yaw = mc.player.getYaw();

        if (forward != 0.0f) {
            if (sideways > 0.0f) {
                yaw += (forward > 0.0f) ? -45 : 45;
            } else if (sideways < 0.0f) {
                yaw += (forward > 0.0f) ? 45 : -45;
            }
            sideways = 0.0f;
            forward = (forward > 0.0f) ? 1.0f : -1.0f;
        }

        double sinYaw = Math.sin(Math.toRadians(yaw + 90.0f));
        double cosYaw = Math.cos(Math.toRadians(yaw + 90.0f));
        double xMovement = forward * distance * cosYaw + sideways * distance * sinYaw;
        double zMovement = forward * distance * sinYaw - sideways * distance * cosYaw;

        return new double[]{xMovement, zMovement};
    }

    public static void setVelocity(float velocity, float y) {
        final double[] direction = MovingUtil.calculateDirection(velocity);
        mc.player.setVelocity(direction[0], y, direction[1]);
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class DirectionalInput {
        public static final DirectionalInput NONE = new DirectionalInput(false, false, false, false);
        public static final DirectionalInput FORWARDS = new DirectionalInput(true, false, false, false);
        public static final DirectionalInput BACKWARDS = new DirectionalInput(false, true, false, false);
        public static final DirectionalInput LEFT = new DirectionalInput(false, false, true, false);
        public static final DirectionalInput RIGHT = new DirectionalInput(false, false, false, true);
        boolean forwards;
        boolean backwards;
        boolean left;
        boolean right;

        public DirectionalInput(Input input) {
            this(input.pressingForward, input.pressingBack, input.pressingLeft, input.pressingRight);
        }

        public static float getMovementMultiplier(boolean positive, boolean negative) {
            if (positive == negative) {
                return 0.0F;
            } else {
                return positive ? 1.0F : -1.0F;
            }
        }
    }
}
