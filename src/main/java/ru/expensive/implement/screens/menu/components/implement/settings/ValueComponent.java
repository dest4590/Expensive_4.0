package ru.expensive.implement.screens.menu.components.implement.settings;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.ValueSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.other.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;
import static ru.expensive.common.util.math.MathUtil.isHovered;

public class ValueComponent extends AbstractSettingComponent {
    public static final int SLIDER_WIDTH = 45;

    private final ValueSetting setting;

    private boolean dragging;
    private double animation;

    public ValueComponent(ValueSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        String wrapped = StringUtil.wrap(setting.getDescription(), 75, 12);
        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        String value = String.valueOf(setting.getValue())
                .replace(".0", "");

        Fonts.getSize(12, BOLD).drawString(context.getMatrices(), value,
                x + width - 9 - Fonts.getSize(12).getStringWidth(value), y + 8, 0xFF8187FF);

        changeValue(
                getDifference(mouseX, positionMatrix));

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(context.getMatrices(), wrapped, x + 9, y + 15, 0xFF878894);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        dragging = isHovered(mouseX, mouseY, x + width - SLIDER_WIDTH - 9, y + 13, SLIDER_WIDTH, 4)
                && button == 0;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private float getDifference(int mouseX, Matrix4f positionMatrix) {
        float percentValue = SLIDER_WIDTH * (setting.getValue() - setting.getMin())
                / (setting.getMax() - setting.getMin()),
                difference = MathHelper.clamp(mouseX - (x + width - SLIDER_WIDTH - 9), 0, SLIDER_WIDTH);

        animation = MathHelper.lerp(0.9F, animation, percentValue);

        rectangle.render(ShapeProperties
                .create(positionMatrix, x + width - SLIDER_WIDTH - 9, y + 15, SLIDER_WIDTH, 1)
                .color(0x2D2E414D)
                .build());

        rectangle.render(ShapeProperties
                .create(positionMatrix, x + width - SLIDER_WIDTH - 9, y + 15, (float) animation, 1)
                .color(0xFF8187FF, 0xFF8187FF, 0xFF4D5199, 0xFF4D5199)
                .build());

        float v = MathHelper.clamp((float) (x + width - SLIDER_WIDTH + animation), 0, x + width - 4);
        rectangle.render(ShapeProperties.create(positionMatrix, v - 10, y + 12.5F, 6, 6)
                .round(6)
                .color(0xFF000000)
                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, v - 8.8F, y + 13.5F, 4, 4)
                .round(4)
                .color(0xFF8187FF)
                .build());
        return difference;
    }

    private void changeValue(float difference) {
        BigDecimal bd = BigDecimal
                .valueOf((difference / SLIDER_WIDTH) * (setting.getMax() - setting.getMin())
                        + setting.getMin())
                .setScale(1, RoundingMode.HALF_UP);

        if (dragging) {
            setting.setValue(difference == 0 ? setting.getMin() : bd.floatValue());
        }
    }
}
