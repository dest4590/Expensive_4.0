package ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color.component;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import static net.minecraft.util.math.MathHelper.clamp;

@RequiredArgsConstructor
public class SaturationComponent extends AbstractComponent {
    private final ColorSetting setting;
    private boolean saturationDragging;

    private float X, Y, W, H;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        X = x + 6;
        Y = y + 73.5F;
        W = 138;
        H = 4;

        float clampedX = clamp(X + W * setting.getHue(), X, X + W - 4);
        float min = clamp((mouseX - X) / W, 0, 1);

        image.setMatrixStack(context.getMatrices())
                .setTexture("textures/hue.png")
                .render(
                        ShapeProperties.create(positionMatrix, X, Y + 0.5, W, H - 1)
                                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, clampedX, Y, H, H)
                .round(H)
                .thickness(3)
                .color(0x00FFFFFF)
                .outlineColor(0xFFFFFFFF)
                .build());

        if (saturationDragging) {
            setting.setHue(min);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        saturationDragging = button == 0 && MathUtil.isHovered(mouseX, mouseY, X, Y, W, H);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        saturationDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
