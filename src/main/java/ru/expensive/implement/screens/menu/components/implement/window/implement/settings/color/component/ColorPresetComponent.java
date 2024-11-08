package ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color.component;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color.ColorPresetButton;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ColorPresetComponent extends AbstractComponent {
    private final List<ColorPresetButton> colorPresetButtonList = new ArrayList<>();
    private final ColorSetting setting;
    private float windowHeight;

    public ColorPresetComponent(ColorSetting setting) {
        this.setting = setting;

        for (int preset : setting.getPresets()) {
            colorPresetButtonList.add(new ColorPresetButton(setting, preset));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!colorPresetButtonList.isEmpty()) {
            Fonts.getSize(11).drawString(context.getMatrices(), "Presets", x + 6, y + 112, -1);
        }

        int xOffset = 0,
                yOffset = 0;

        int colorIndex = 0;
        int size = 13;

        for (ColorPresetButton button : colorPresetButtonList) {
            button.x = x + 6 + xOffset;
            button.y = y + 120 + yOffset;
            button.render(context, mouseX, mouseY, delta);

            xOffset += size;
            colorIndex++;

            if (colorIndex >= 11) {
                colorIndex = 0;
                xOffset = 0;
                yOffset += size - 1;
            }
        }

        windowHeight = colorPresetButtonList.isEmpty() ? 132 : 156 + yOffset - (float) yOffset / 2;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        colorPresetButtonList.forEach(colorPresetButton -> colorPresetButton.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
