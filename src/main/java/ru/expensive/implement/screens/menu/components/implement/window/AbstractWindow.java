package ru.expensive.implement.screens.menu.components.implement.window;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.Direction;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

public abstract class AbstractWindow extends AbstractComponent {
    @Getter
    private final Animation scaleAnimation = new DecelerateAnimation()
            .setValue(1)
            .setMs(100);
    private boolean dragging, draggable;
    private int dragX, dragY;

    public AbstractWindow() {
        scaleAnimation.setDirection(Direction.FORWARDS);
    }

    public AbstractWindow draggable(boolean draggable) {
        this.draggable = draggable;
        return this;
    }

    @Override
    public AbstractWindow size(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public AbstractWindow position(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0 && draggable) {
            dragging = true;
            dragX = (int) (x - mouseX);
            dragY = (int) (y - mouseY);
            return true;
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (dragging && draggable) {
            x = mouseX + dragX;
            y = mouseY + dragY;
        }

        float scale = scaleAnimation.getOutput().floatValue();
        MathUtil.scale(context.getMatrices(), x + width / 2, y + height / 2, scale, () -> {
            drawWindow(context, mouseX, mouseY, delta);
        });
    }

    protected abstract void drawWindow(DrawContext context, int mouseX, int mouseY, float delta);

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return true;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void startCloseAnimation() {
        scaleAnimation.setDirection(Direction.BACKWARDS);
    }

    public boolean isCloseAnimationFinished() {
        return scaleAnimation.isFinished(Direction.BACKWARDS);
    }
}