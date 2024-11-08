package ru.expensive.implement.screens.menu.components.implement.settings;

import net.minecraft.client.gui.DrawContext;
import ru.expensive.api.feature.module.setting.implement.ButtonSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.implement.screens.menu.components.implement.other.ButtonComponent;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class SButtonComponent extends AbstractSettingComponent {
    private final ButtonComponent buttonComponent = new ButtonComponent();
    private final ButtonSetting setting;

    public SButtonComponent(ButtonSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        var wrapped = StringUtil.wrap(setting.getDescription(), 80, 12);

        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(context.getMatrices(), wrapped, x + 9, y + 15, 0xFF878894);

        ((ButtonComponent) buttonComponent.setText("Click on me")
                .setRunnable(setting.getRunnable())
                .position(x + width - 9 - buttonComponent.width, y + 5))
                .render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        buttonComponent.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
