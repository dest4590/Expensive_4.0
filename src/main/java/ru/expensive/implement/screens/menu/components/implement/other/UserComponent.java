package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.MenuScreen;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;
import ru.expensive.implement.screens.menu.components.implement.window.implement.module.InfoWindow;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

@Setter
@Accessors(chain = true)
public class UserComponent extends AbstractComponent {
    private MenuScreen menuScreen;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());

        rectangle.render(ShapeProperties.create(positionMatrix, x + 6, y - 25, 15, 15)
                .round(15)
                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, x + 15.5F, y - 15.5F, 6, 6)
                .round(6)
                .thickness(4)
                .outlineColor(0xFF191a28)
                .color(0xFF26c68c)
                .build());

        Fonts.getSize(12, BOLD).drawString(context.getMatrices(), "username", x + 25, y - 21, 0xFFD4D6E1);
        Fonts.getSize(10).drawString(context.getMatrices(), "09.05.2024", x + 25, y - 14.5, 0xFF8187FF);

        image.setTexture("textures/settings.png").render(
                ShapeProperties.create(positionMatrix, x + 72, y - 20, 6.5F, 6.5F)
                        .color(0xFFafb0bc)
                        .build());

        image.setTexture("textures/palette.png").render(
                ShapeProperties.create(positionMatrix, x + 61, y - 20, 7F, 7F)
                        .color(0xFFafb0bc)
                        .build());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x + 72, y - 20, 6.5F, 6.5F) && button == 0) {
            AbstractWindow infoWindow = new InfoWindow()
                    .position(menuScreen.x - 150, menuScreen.y)
                    .size(140, 184);

            windowManager.add(infoWindow);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}
