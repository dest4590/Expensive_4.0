package ru.expensive.implement.screens.menu.components.implement.window;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WindowManager extends AbstractComponent {
    private final List<AbstractWindow> windows = new ArrayList<>();

    public void add(AbstractWindow window) {
        windows.add(window);
    }

    public void delete(AbstractWindow window) {
        window.startCloseAnimation();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        List<AbstractWindow> toRemove = new ArrayList<>();
        windows.forEach(window -> {
            window.render(context, mouseX, mouseY, delta);

            if (window.isCloseAnimationFinished()) {
                toRemove.add(window);
            }
        });
        windows.removeAll(toRemove);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean clickedInsideWindow = false;

        List<AbstractWindow> windowsCopy = new ArrayList<>(windows);

        for (int i = windowsCopy.size() - 1; i >= 0; i--) {
            AbstractWindow window = windowsCopy.get(i);
            if (window.isHovered(mouseX, mouseY) || isHover(mouseX, mouseY)) {
                clickedInsideWindow = true;
                window.mouseClicked(mouseX, mouseY, button);
                break;
            }
        }

        if (!clickedInsideWindow) {
            for (AbstractWindow window : windows) {
                window.startCloseAnimation();

            }
            return false;
        }

        return clickedInsideWindow;
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        windows.forEach(window -> window.isHovered(mouseX, mouseY));

        for (AbstractWindow window : windows) {
            if (window.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return super.isHover(mouseX, mouseY);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        windows.forEach(window -> window.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (AbstractWindow window : windows) {
            if (window.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        windows.forEach(window -> window.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        windows.forEach(window -> window.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
