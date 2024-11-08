package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Expensive;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class WatermarkDraggable extends AbstractDraggable {

    public WatermarkDraggable() {
        super("Watermark", 10, 10, 92, 16);
    }

    @Override
    public boolean visible() {
        return Expensive.getInstance().getModuleProvider()
                .module("Hud")
                .isState();
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        setHeight(10);

        rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                .round(6)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build());

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        image.setTexture("textures/icon_logo.png")
                .render(ShapeProperties.create(positionMatrix, getX() + 3, getY() + 2, 6, 6)
                        .build());

        FontRenderer font = Fonts.getSize(11, BOLD);
        String name = "Expensive 4.0";
        String ms = "112213 ms";
        String fps = mc.getCurrentFps() + " fps";

        image.setTexture("textures/ping.png")
                .render(ShapeProperties
                        .create(positionMatrix, getX() + font.getStringWidth(name) + 12.5,
                                getY() + 0.5, 9, 9)
                        .build());

        image.setTexture("textures/frame.png")
                .render(ShapeProperties
                        .create(positionMatrix,
                                getX() + font.getStringWidth(name) + 24
                                        + font.getStringWidth(ms),
                                getY() + 2, 6, 6)
                        .build());

        font.drawGradientString(context.getMatrices(), name, getX() + 12, getY() + 4.5, 0xFF8187FF, 0xFF4D5199);
        font.drawString(context.getMatrices(), ms, getX() + font.getStringWidth(name) + 22, getY() + 4.5, -1);
        font.drawString(context.getMatrices(), fps,
                getX() + font.getStringWidth(name) + 24 + font.getStringWidth(ms) + 8, getY() + 4.5,
                -1);

        setWidth((int) (font.getStringWidth(name + ms + fps) + 36));
    }
}
