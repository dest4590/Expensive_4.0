package ru.expensive.implement.screens.title;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;

public class AccountManagerScreen extends Screen implements QuickImports {
    protected AccountManagerScreen() {
        super(Text.empty());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());

        image.setTexture("textures/mainmenu.png").render(ShapeProperties.create(positionMatrix, 0, 0, width, height)
                .build());

        image.setTexture("textures/big_logo.png")
                .render(ShapeProperties
                        .create(positionMatrix, window.getScaledWidth() - 400, window.getScaledHeight() - 480, 618, 618)
                        .build());

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
