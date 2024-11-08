package ru.expensive.implement.screens.title.button.implement;

import net.minecraft.client.gui.DrawContext;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.title.button.AbstractButton;

public class CustomTextTitleButton extends AbstractButton {
    public CustomTextTitleButton(String name, Runnable action) {
        super(name, action);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        width = Fonts.getSize(16, Fonts.Type.DEFAULT).getStringWidth(name);
        height = Fonts.getSize(16, Fonts.Type.DEFAULT).getStringHeight(name);

        Fonts.getSize(16, Fonts.Type.DEFAULT).drawString(context.getMatrices(), name, x, y, -1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            action.run();
        }
        return false;
    }
}
