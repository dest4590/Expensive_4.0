package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;

public class SearchComponent extends AbstractComponent {
    @Getter
    private String text = "";

    private int cursorPosition = 0;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private boolean isTyping = false;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        width = 80;
        height = 15;

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(15)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF11121C)
                .color(0x54191A28)
                .build());

        image.setTexture("textures/search.png").render(
                ShapeProperties.create(positionMatrix, x + width - 12, y + 5, 5F, 5F)
                        .build());

        FontRenderer fontRenderer = Fonts.getSize(12);
        String displayText = text.equalsIgnoreCase("") && !isTyping ? "Search" : text;
        fontRenderer.drawString(context.getMatrices(), displayText, x + 7, y + 6.5, 0xFF878894);

        if (isTyping) {
            float cursorX = x + 7 + fontRenderer.getStringWidth(text.substring(0, cursorPosition));
            rectangle.render(ShapeProperties.create(positionMatrix, cursorX, y + 4, 0.5, height - 8)
                    .color(0xFFFFFFFF)
                    .build());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height)) {
            cursorPosition = getClickedPosition(mouseX);
            selectionStart = -1;
            selectionEnd = -1;
            isTyping = true;
            return true;
        } else {
            isTyping = false;
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (isTyping && Fonts.getSize(12).getStringWidth(text) < 55) {
            updateText(chr);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isTyping) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_BACKSPACE -> handleBackspace();
                case GLFW.GLFW_KEY_LEFT -> {
                    if (cursorPosition > 0) {
                        cursorPosition--;
                    }
                }
                case GLFW.GLFW_KEY_RIGHT -> {
                    if (cursorPosition < text.length()) {
                        cursorPosition++;
                    }
                }
                case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_ESCAPE -> isTyping = false;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isTyping && button == 0 && selectionStart != -1 && selectionEnd == -1) {
            selectionEnd = cursorPosition;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void handleBackspace() {
        if (cursorPosition > 0) {
            if (selectionStart != -1 && selectionEnd != -1) {
                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);
                text = text.substring(0, start) + text.substring(end);
                cursorPosition = start;
                selectionStart = -1;
                selectionEnd = -1;
            } else {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }
        }
    }

    private void updateText(char chr) {
        if (selectionStart != -1 && selectionEnd != -1) {
            int start = Math.min(selectionStart, selectionEnd);
            int end = Math.max(selectionStart, selectionEnd);
            text = text.substring(0, start) + chr + text.substring(end);
            cursorPosition = start + 1;
            selectionStart = -1;
            selectionEnd = -1;
        } else {
            text = text.substring(0, cursorPosition) + chr + text.substring(cursorPosition);
            cursorPosition++;
        }
    }

    private int getClickedPosition(double mouseX) {
        FontRenderer fontRenderer = Fonts.getSize(12);
        int relativeX = (int) (mouseX - x - 7);
        for (int i = 0; i <= text.length(); i++) {
            if (fontRenderer.getStringWidth(text.substring(0, i)) > relativeX) {
                return i;
            }
        }
        return text.length();
    }
}
