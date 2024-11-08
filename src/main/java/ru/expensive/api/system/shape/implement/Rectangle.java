package ru.expensive.api.system.shape.implement;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Tessellator;
import org.joml.Vector4f;
import org.joml.Vector4i;
import ru.expensive.api.system.shape.Shape;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.QuickImports;

import static ru.expensive.api.system.shader.ShadersPool.ROUNDED_SHADER;
import static ru.expensive.common.util.math.MathUtil.colorToArray;

public class Rectangle implements Shape, QuickImports {

    @Override
    public void render(ShapeProperties shapeProperties) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float softness = shapeProperties.getSoftness();

        drawEngine.quad(shapeProperties.getMatrix4f(), shapeProperties.getX() - softness / 2,
                shapeProperties.getY() - softness / 2,
                shapeProperties.getWidth() + softness,
                shapeProperties.getHeight() + softness);

        int scale = mc.options.getGuiScale()
                .getValue();

        ROUNDED_SHADER.size.set(shapeProperties.getWidth() * scale, shapeProperties.getHeight() * scale);
        ROUNDED_SHADER.location.set(shapeProperties.getX() * scale, window.getHeight()
                - (shapeProperties.getHeight() * scale) - (shapeProperties.getY() * scale));

        Vector4f round = shapeProperties.getRound();

        ROUNDED_SHADER.radius.set(
                round.x,
                round.y,
                round.z,
                round.w);

        ROUNDED_SHADER.softness.set(softness);
        ROUNDED_SHADER.thickness.set(shapeProperties.getThickness());

        Vector4i gradientColor = shapeProperties.getColor();

        ROUNDED_SHADER.color1.set(colorToArray(gradientColor.x));
        ROUNDED_SHADER.color2.set(colorToArray(gradientColor.y));
        ROUNDED_SHADER.color3.set(colorToArray(gradientColor.z));
        ROUNDED_SHADER.color4.set(colorToArray(gradientColor.w));

        ROUNDED_SHADER.outlineColor.set(colorToArray(shapeProperties.getOutlineColor()));

        ROUNDED_SHADER.use();

        Tessellator.getInstance().draw();
        RenderSystem.disableBlend();
    }
}
