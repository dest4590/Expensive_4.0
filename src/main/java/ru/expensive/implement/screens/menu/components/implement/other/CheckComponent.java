package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.render.Stencil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import static ru.expensive.api.system.animation.Direction.BACKWARDS;
import static ru.expensive.api.system.animation.Direction.FORWARDS;

@Setter
@Accessors(chain = true)
public class CheckComponent extends AbstractComponent {
    private final Animation alphaAnimation = new DecelerateAnimation()
            .setMs(300)
            .setValue(255);
    private final Animation stencilAnimation = new DecelerateAnimation()
            .setMs(200)
            .setValue(8);
    private boolean state;
    private Runnable runnable;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        alphaAnimation.setDirection(state ? FORWARDS : BACKWARDS);
        stencilAnimation.setDirection(state ? FORWARDS : BACKWARDS);

        int stateColor = state
                ? 0xFF8187ff
                : 0x00161725;

        int outlineStateColor = state
                ? 0xFF8187ff
                : 0xFF282932;

        int opacity = alphaAnimation
                .getOutput()
                .intValue();

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, 8, 8)
                .round(3)
                .thickness(2)
                .outlineColor(outlineStateColor)
                .color(MathUtil.applyOpacity(stateColor, opacity))
                .build());

        Stencil.push();
        rectangle.render(
                ShapeProperties.create(positionMatrix, x, y, stencilAnimation.getOutput().intValue(), 8)
                        .build());
        Stencil.read(1);
        image.setMatrixStack(context.getMatrices())
                .setTexture("textures/check.png")
                .render(
                        ShapeProperties.create(positionMatrix, x + 2, y + 2, 4, 4)
                                .color(MathUtil.applyOpacity(0xFFFFFFFF, opacity))
                                .build());

        Stencil.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x, y, 8, 8) && button == 0) {
            runnable.run();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
