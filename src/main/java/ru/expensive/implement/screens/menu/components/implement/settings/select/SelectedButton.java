package ru.expensive.implement.screens.menu.components.implement.settings.select;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.Direction;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;
import static ru.expensive.common.util.math.MathUtil.applyOpacity;
import static ru.expensive.common.util.math.MathUtil.isHovered;

public class SelectedButton extends AbstractComponent {
    private final SelectSetting setting;
    private final String text;
    private final Animation alphaAnimation = new DecelerateAnimation()
            .setMs(300)
            .setValue(0x2D);
    @Setter
    @Accessors(chain = true)
    private int alpha;

    public SelectedButton(SelectSetting setting, String text) {
        this.setting = setting;
        this.text = text;

        alphaAnimation.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        alphaAnimation.setDirection(setting.getSelected().contains(text)
                ? Direction.FORWARDS
                : Direction.BACKWARDS);

        alphaAnimation.setMs(400);

        int opacity = alphaAnimation
                .getOutput()
                .intValue();

        int selectedOpacity = applyOpacity(
                applyOpacity(0xFF2D2E41, opacity),
                alpha);

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .color(selectedOpacity)
                .build());

        int checkOpacity = applyOpacity(
                applyOpacity(-1, opacity * 5),
                alpha);

        image.setMatrixStack(matrices)
                .setTexture("textures/check.png")
                .render(ShapeProperties.create(positionMatrix, x + width - 8, y + 4.5F, 4, 4)
                        .color(checkOpacity)
                        .build());

        Fonts.getSize(12, BOLD).drawString(matrices, text, x + 4, y + 5, applyOpacity(0xFFD4D6E1, alpha));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            setting.setSelected(text);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
