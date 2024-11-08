package ru.expensive.implement.screens.title.button.implement;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Rectangle;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.title.button.AbstractButton;

public class CustomTitleButton extends AbstractButton {

    public CustomTitleButton(String name, Runnable action) {
        super(name, action);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();

        int color = MathUtil.isHovered(mouseX, mouseY, x, y, width, height)
                ? 0xFF232431
                : 0xFF191a28;

        new Rectangle().render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(5)
                .thickness(2)
                .outlineColor(0xFF2d2e41)
                .color(color)
                .build());

        Fonts.getSize(16, Fonts.Type.DEFAULT).drawCenteredString(context.getMatrices(), name, x + width / 2 - 1,
                y + height / 2 - 2, -1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            action.run();
        }
        return false;
    }
}
