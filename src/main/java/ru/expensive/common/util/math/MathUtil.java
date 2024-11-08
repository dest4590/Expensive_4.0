package ru.expensive.common.util.math;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.ColorHelper;
import org.joml.Vector3d;
import ru.expensive.common.QuickImports;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtil implements QuickImports {

    public static boolean isHovered(double mouseX,
                                    double mouseY,
                                    double x,
                                    double y,
                                    double width,
                                    double height) {
        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
    }

    public static double computeGcd() {
        double f = mc.options.getMouseSensitivity().getValue() * 0.6 + 0.2;
        return f * f * f * 8.0 * 0.15;
    }

    public static double getRandom(double min, double max) {
        if (min == max) {
            return min;
        } else {
            if (min > max) {
                double d = min;
                min = max;
                max = d;
            }

            return ThreadLocalRandom.current().nextDouble(min, max);
        }
    }

    public static void scale(MatrixStack stack,
                             float x,
                             float y,
                             float scale,
                             Runnable data) {

        stack.push();
        stack.translate(x, y, 0);
        stack.scale(scale, scale, 1);
        stack.translate(-x, -y, 0);
        data.run();
        stack.pop();
    }

    public static double round(double num, double increment) {
        double rounded = Math.round(num / increment) * increment;
        return Math.round(rounded * 100.0) / 100.0;
    }

    public static int floorNearestMulN(int x, int n) {
        return n * (int) Math.floor((double) x / (double) n);
    }

    public static int getRed(int hex) {
        return hex >> 16 & 255;
    }

    public static int getGreen(int hex) {
        return hex >> 8 & 255;
    }

    public static int getBlue(int hex) {
        return hex & 255;
    }

    public static int getAlpha(int hex) {
        return hex >> 24 & 255;
    }

    public static float[] colorToArray(int hex) {
        float[] rgba = new float[4];

        rgba[0] = getRed(hex) / 255f;
        rgba[1] = getGreen(hex) / 255f;
        rgba[2] = getBlue(hex) / 255f;
        rgba[3] = getAlpha(hex) / 255f;

        return rgba;
    }

    public static int applyOpacity(int color, float opacity) {
        return ColorHelper.Argb.getArgb((int) (getAlpha(color) * opacity / 255), getRed(color), getGreen(color),
                getBlue(color));
    }

    public static Vector3d getEntityPos(Entity entity) {
        float partialTicks = mc.getTickDelta();

        double x = entity.lastRenderX + (entity.getX() - entity.lastRenderX) * partialTicks,
                y = entity.lastRenderY + (entity.getY() - entity.lastRenderY) * partialTicks,
                z = entity.lastRenderZ + (entity.getZ() - entity.lastRenderZ) * partialTicks;

        return new Vector3d(x, y, z);
    }
}
