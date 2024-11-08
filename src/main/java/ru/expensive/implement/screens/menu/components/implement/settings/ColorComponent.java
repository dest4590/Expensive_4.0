package ru.expensive.implement.screens.menu.components.implement.settings;

import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;
import ru.expensive.implement.screens.menu.components.implement.window.implement.settings.color.ColorWindow;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class ColorComponent extends AbstractSettingComponent {
    private final ColorSetting setting;

    public ColorComponent(ColorSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        String wrapped = StringUtil.wrap(setting.getDescription(), 100, 12);
        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(context.getMatrices(), wrapped, x + 9, y + 15, 0xFF878894);

        rectangle.render(ShapeProperties.create(positionMatrix, x + width - 17.5F, y + 6.2F, 8, 8)
                .round(8)
                .softness(10)
                .color(MathUtil.applyOpacity(setting.getColor(), 60))
                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, x + width - 17.5F, y + 6.2F, 8, 8)
                .round(8)
                .color(0xFF000000)
                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, x + width - 16.5F, y + 7.2F, 6, 6)
                .round(6)
                .color(setting.getColor())
                .build());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x + width - 17, y + 6.7F, 7, 7) && button == 0) {
            AbstractWindow existingWindow = null;

            for (AbstractWindow window : windowManager.getWindows()) {
                if (window instanceof ColorWindow) {
                    existingWindow = window;
                    break;
                }
            }

            if (existingWindow != null) {
                windowManager.delete(existingWindow);
            } else {
                AbstractWindow colorWindow = new ColorWindow(setting)
                        .position((int) (mouseX + 20), (int) (mouseY - 82))
                        .size(150, 165)
                        .draggable(true);

                windowManager.add(colorWindow);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
