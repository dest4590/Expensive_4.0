package ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color.component;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

@RequiredArgsConstructor
public class ColorEditorComponent extends AbstractComponent {
    private final ColorSetting setting;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        rectangle.render(ShapeProperties.create(positionMatrix, x + 6, y + 90.5F, 31, 14)
                .round(3)
                .thickness(2)
                .color(0x80222336)
                .outlineColor(0xFF2D2E41)
                .build());

        Fonts.getSize(13).drawString(context.getMatrices(), "HEX", x + 10, y + 96, -1);

        rectangle.render(ShapeProperties.create(positionMatrix, x + 40, y + 90.5F, 80, 14)
                .round(3)
                .thickness(2)
                .color(0x80222336)
                .outlineColor(0xFF2D2E41)
                .build());

        Fonts.getSize(13).drawString(context.getMatrices(), "#" + Integer.toHexString(setting.getColor()), x + 45,
                y + 96, -1);

        rectangle.render(ShapeProperties.create(positionMatrix, x + 122, y + 90.5F, 22, 14)
                .round(3)
                .thickness(2)
                .color(0x80222336)
                .outlineColor(0xFF2D2E41)
                .build());

        int displayValue = (int) (setting.getAlpha() * 100);
        Fonts.getSize(13).drawCenteredString(context.getMatrices(), displayValue + "%", x + 133, y + 96, -1);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (MathUtil.isHovered(mouseX, mouseY, x + 122, y + 90.5F, 22, 14)) {
            setting.setAlpha(MathHelper.clamp((float) (setting.getAlpha() - (amount * 2) / 100), 0, 1));
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
