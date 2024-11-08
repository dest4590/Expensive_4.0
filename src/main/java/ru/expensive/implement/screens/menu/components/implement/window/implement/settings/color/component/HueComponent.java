package ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color.component;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import java.awt.*;

import static net.minecraft.util.math.MathHelper.clamp;

@RequiredArgsConstructor
public class HueComponent extends AbstractComponent {
    private final ColorSetting setting;
    private boolean hueDragging;

    private float X, Y, W, H;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        X = x + 6;
        Y = y + 18.5F;
        W = 138;
        H = 50;

        int[] color = {
                0xFF000000,
                0xFFFFFFFF,
                0xFF000000,
                Color.HSBtoRGB(setting.getHue(), 1, 1)
        };

        rectangle.render(ShapeProperties.create(positionMatrix, X, Y, W, H)
                .round(4)
                .color(color)
                .build());

        float clampedX = clamp(X + W * setting.getSaturation(), X, X + W - 5);
        float clampedY = clamp(Y + H * (1 - setting.getBrightness()), Y, Y + H - 5);

        rectangle.render(ShapeProperties.create(positionMatrix, clampedX, clampedY, 5, 5)
                .round(5)
                .softness(1)
                .thickness(3)
                .color(0x00FFFFFF)
                .outlineColor(0xFFFFFFFF)
                .build());

        float min = clamp((mouseX - X) / W, 0, 1);

        if (hueDragging) {
            setting.setBrightness(clamp(1 - ((mouseY - Y) / H), 0, 1));
            setting.setSaturation(min);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        hueDragging = button == 0 && MathUtil.isHovered(mouseX, mouseY, X, Y, W, H);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        hueDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
