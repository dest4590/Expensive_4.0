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

public class CoordsDraggable extends AbstractDraggable {

    public CoordsDraggable() {
        super("Coords", 320, 10, 72, 10);
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

        rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                .round(6)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, getX() + 10.5, getY() + 3, 0.8, 4)
                .color(0xFF2D2E41)
                .build());

        String coordinate = "x:" + (int) mc.player.getX() + "  " +
                "y:" + (int) mc.player.getY() + "  " +
                "z:" + (int) mc.player.getZ();

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        image.setTexture("textures/world.png")
                .render(ShapeProperties.create(positionMatrix, getX() + 3.5, getY() + 2.5, 5, 5)
                        .build());

        FontRenderer fontRenderer = Fonts.getSize(10, BOLD);
        fontRenderer.drawString(context.getMatrices(), coordinate, getX() + 13, getY() + 4.5, -1);

        setWidth((int) (fontRenderer.getStringWidth(coordinate) + 17));
    }
}
