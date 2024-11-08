package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.implement.screens.menu.MenuScreen;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

@Setter
@Accessors(chain = true)
public class BackgroundComponent extends AbstractComponent {
    private MenuScreen menuScreen;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());

        image.setTexture("textures/background.png")
                .render(
                        ShapeProperties.create(positionMatrix, x, y, width, height)
                                .build());

        image.setTexture("textures/small_logo.png").render(
                ShapeProperties.create(positionMatrix, x + 13, y + 20, 58, 11)
                        .build());

        Fonts.getSize(16, BOLD).drawString(context.getMatrices(), menuScreen.category.getReadableName(), x + 95,
                y + 13, 0xFFD4D6E1);
    }
}
