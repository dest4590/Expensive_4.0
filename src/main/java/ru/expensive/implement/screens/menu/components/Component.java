package ru.expensive.implement.screens.menu.components;

import net.minecraft.client.gui.DrawContext;

public interface Component {
    void render(DrawContext context, int mouseX, int mouseY, float delta);

    void tick();

    boolean mouseClicked(double mouseX, double mouseY, int button);

    boolean mouseReleased(double mouseX, double mouseY, int button);

    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);

    boolean mouseScrolled(double mouseX, double mouseY, double amount);

    boolean keyPressed(int keyCode, int scanCode, int modifiers);

    boolean charTyped(char chr, int modifiers);

    boolean isHover(double mouseX, double mouseY);
}
