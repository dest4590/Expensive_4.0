package ru.expensive.implement.screens.title.button;

import net.minecraft.client.gui.DrawContext;

public interface Button {
    void render(DrawContext context, int mouseX, int mouseY, float delta);

    boolean mouseClicked(double mouseX, double mouseY, int button);
}
