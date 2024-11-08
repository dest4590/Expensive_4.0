package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

@Setter
@Accessors(chain = true)
public class ButtonComponent extends AbstractComponent {
    private String text;
    private Runnable runnable;
    private int color = 0xFF8187FF;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        width = Fonts.getSize(12).getStringWidth(text) + 13;
        height = 12;

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(4)
                .color(color)
                .build());

        Fonts.getSize(12, BOLD).drawCenteredString(context.getMatrices(), text, x + (double) width / 2, y + 5, -1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            runnable.run();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
