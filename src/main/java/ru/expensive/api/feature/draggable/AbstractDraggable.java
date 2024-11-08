package ru.expensive.api.feature.draggable;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.Direction;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.common.QuickImports;
import ru.expensive.common.QuickLogger;

@Setter
@Getter
public abstract class AbstractDraggable implements Draggable, QuickImports, QuickLogger {
    private final Animation scaleAnimation = new DecelerateAnimation()
            .setValue(1)
            .setMs(150);
    private String name;
    private int x, y, width, height;
    private boolean dragging;
    private int dragX, dragY;

    public AbstractDraggable(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean visible() {
        return false;
    }

    @Override
    public void tick(float delta) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            dragging = true;
            dragX = x - (int) mouseX;
            dragY = y - (int) mouseY;
            return true;
        }
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        x = calculateCenteredX(mouseX);
        y = calculateCenteredY(mouseY);
    }

    private int calculateCenteredX(float mouseX) {
        int x = (int) Math.max(0, Math.min(mouseX + dragX, window.getScaledWidth() - width));
        int edgeRadius = 2;
        int centerRadius = 10;

        int windowWidth = window.getScaledWidth();

        if (x <= edgeRadius) {
            x = 0;
        } else if (x >= windowWidth - width - edgeRadius) {
            x = windowWidth - width;
        } else if (Math.abs(x + (float) width / 2 - (float) windowWidth / 2) <= centerRadius) {
            x = (windowWidth - width) / 2;
        }
        return x;
    }

    private int calculateCenteredY(float mouseY) {
        int y = (int) Math.max(0, Math.min(mouseY + dragY, window.getScaledHeight() - height));
        int edgeRadius = 2;
        int centerRadius = 10;
        int windowHeight = window.getScaledHeight();

        if (y <= edgeRadius) {
            y = 0;
        } else if (y >= windowHeight - height - edgeRadius) {
            y = windowHeight - height;
        } else if (Math.abs(y + height / 2 - windowHeight / 2) <= centerRadius) {
            y = (windowHeight - height) / 2;
        }
        return y;
    }

    public abstract void drawDraggable(DrawContext context);

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

    public void startAnimation() {
        scaleAnimation.setDirection(Direction.FORWARDS);
    }

    public boolean isCloseAnimationFinished() {
        return scaleAnimation.isFinished(Direction.BACKWARDS);
    }
}
