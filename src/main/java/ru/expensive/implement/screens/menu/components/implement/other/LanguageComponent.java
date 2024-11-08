package ru.expensive.implement.screens.menu.components.implement.other;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.core.Expensive;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

public class LanguageComponent extends AbstractComponent {

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        width = 31;
        height = 15;

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(15)
                .softness(1)
                .thickness(2)
                .outlineColor(0xAF11121C)
                .color(0x1A191A28)
                .build());

        image.setTexture("textures/locate.png").render(
                ShapeProperties.create(positionMatrix, x + 5, y + 4.5, 6F, 6F)
                        .build());

        Fonts.getSize(12).drawString(context.getMatrices(), Expensive.getInstance().getLanguage().name(), x + 13.3,
                y + 6.5, 0xFF878894);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            Expensive.getInstance().toggleLanguage();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
