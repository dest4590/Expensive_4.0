package ru.expensive.implement.screens.menu.components.implement.settings;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import ru.expensive.api.feature.module.setting.implement.TextSetting;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.common.util.render.Stencil;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TextComponent extends AbstractSettingComponent {
    @NonFinal
    public static boolean typing;
    TextSetting setting;
    @NonFinal
    float rectX, rectY, rectWidth, rectHeight;
    @NonFinal
    boolean dragging;
    @NonFinal
    String text = "";
    @NonFinal
    int cursorPosition = 0;
    @NonFinal
    int selectionStart = -1;
    @NonFinal
    int selectionEnd = -1;
    @NonFinal
    long lastClickTime = 0;
    @NonFinal
    float xOffset = 0;
    @NonFinal
    long lastInputTime = System.currentTimeMillis();

    public TextComponent(TextSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        String wrapped = StringUtil.wrap(setting.getDescription(), 70, 12);
        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        this.rectX = x + width - 61.5F;
        this.rectY = y + 6.0F;
        this.rectWidth = 53.0F;
        this.rectHeight = 12.0F;

        rectangle.render(ShapeProperties.create(positionMatrix, rectX, rectY, rectWidth, rectHeight)
                .round(6)
                .thickness(2)
                .color(0xFF161825)
                .outlineColor(0x2D2D2E41)
                .build());

        int min = setting.getMin();
        int max = setting.getMax();

        int color = (min > text.length() || max < text.length())
                ? 0xFF878894
                : 0xFF10C97B;

        image.setMatrixStack(context.getMatrices())
                .setTexture("textures/check.png")
                .render(ShapeProperties
                        .create(positionMatrix, rectX + rectWidth - 8, rectY + (rectHeight / 2) - 2, 4, 4)
                        .color(color)
                        .build());

        Fonts.getSize(14, Fonts.Type.BOLD).drawString(context.getMatrices(), setting.getName(), x + 9, y + 6,
                0xFFD4D6E1);
        Fonts.getSize(12).drawString(context.getMatrices(), wrapped, x + 9, y + 15, 0xFF878894);

        FontRenderer font = Fonts.getSize(12, Fonts.Type.BOLD);
        updateXOffset(font, cursorPosition);

        Stencil.push();
        rectangle.render(ShapeProperties.create(positionMatrix, (rectX + 3), rectY, (rectWidth - 15), rectHeight)
                .color(-1)
                .build());
        Stencil.read(1);
        if (selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd) {
            int start = Math.max(0, Math.min(getStartOfSelection(), text.length()));
            int end = Math.max(0, Math.min(getEndOfSelection(), text.length()));
            if (start < end) {
                float selectionXStart = rectX + 3 - xOffset + font.getStringWidth(text.substring(0, start));
                float selectionXEnd = rectX + 3 - xOffset + font.getStringWidth(text.substring(0, end));
                float selectionWidth = selectionXEnd - selectionXStart;

                rectangle.render(ShapeProperties
                        .create(positionMatrix, selectionXStart, rectY + (rectHeight / 2) - 5.0F, selectionWidth, 10.0F)
                        .round(0)
                        .thickness(0)
                        .softness(0)
                        .color(0xFF5585E8)
                        .build());
            }
        }

        font.drawString(context.getMatrices(), text, rectX + 3 - xOffset, rectY + (rectHeight / 2) - 1.0F,
                typing ? -1 : 0xFF878894);

        if (!typing && text.isEmpty()) {
            font.drawString(context.getMatrices(), setting.getText(), rectX + 3, rectY + (rectHeight / 2) - 1.0F,
                    0xFF878894);
        }
        Stencil.pop();
        long currentTime = System.currentTimeMillis();
        boolean focused = typing && (currentTime - lastInputTime < 500 || currentTime % 1000 < 500);

        if (focused && (selectionStart == -1 || selectionStart == selectionEnd)) {
            float cursorX = font.getStringWidth(text.substring(0, cursorPosition));
            font.drawString(context.getMatrices(), "|", rectX + 3 - xOffset + cursorX, rectY + (rectHeight / 2) - 1.0F,
                    -1);
        }

        if (dragging) {
            cursorPosition = getCursorIndexAt(mouseX);

            if (selectionStart == -1) {
                selectionStart = cursorPosition + 1;
            }
            selectionEnd = cursorPosition;
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        dragging = true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, rectX, rectY, rectWidth, rectHeight) && button == 0) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < 250) {
                selectionStart = 0;
                selectionEnd = text.length();
            } else {
                typing = true;
                dragging = true;
                lastClickTime = currentTime;
                cursorPosition = getCursorIndexAt(mouseX);
                selectionStart = cursorPosition;
                selectionEnd = cursorPosition;
            }
        } else {
            typing = false;
            clearSelection();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (typing && (text.length() < setting.getMax())) {
            deleteSelectedText();
            text = text.substring(0, cursorPosition) + chr + text.substring(cursorPosition);
            cursorPosition++;
            clearSelection();
            lastInputTime = System.currentTimeMillis();
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (typing) {
            if (Screen.hasControlDown()) {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_A -> selectAllText();
                    case GLFW.GLFW_KEY_V -> pasteFromClipboard();
                    case GLFW.GLFW_KEY_C -> copyToClipboard();
                }
            } else {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_BACKSPACE, GLFW.GLFW_KEY_ENTER -> handleTextModification(keyCode);
                    case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT -> moveCursor(keyCode);
                }
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void pasteFromClipboard() {
        String clipboardText = GLFW.glfwGetClipboardString(window.getHandle());
        if (clipboardText != null) {
            replaceText(cursorPosition, cursorPosition, clipboardText);
        }
    }

    private void copyToClipboard() {
        if (hasSelection()) {
            GLFW.glfwSetClipboardString(window.getHandle(), getSelectedText());
        }
    }

    private void selectAllText() {
        selectionStart = 0;
        selectionEnd = text.length();
    }

    private void handleTextModification(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (hasSelection()) {
                replaceText(getStartOfSelection(), getEndOfSelection(), "");
            } else if (cursorPosition > 0) {
                replaceText(cursorPosition - 1, cursorPosition, "");
            }
        } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            if (text.length() >= setting.getMin() && text.length() <= setting.getMax()) {
                setting.setText(text);
                typing = false;
            }
        }
    }

    private void moveCursor(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_LEFT && cursorPosition > 0) {
            cursorPosition--;
        } else if (keyCode == GLFW.GLFW_KEY_RIGHT && cursorPosition < text.length()) {
            cursorPosition++;
        }
        updateSelectionAfterCursorMove();
    }

    private void updateSelectionAfterCursorMove() {
        if (Screen.hasShiftDown()) {
            if (selectionStart == -1)
                selectionStart = cursorPosition;
            selectionEnd = cursorPosition;
        } else {
            clearSelection();
        }
        lastInputTime = System.currentTimeMillis();
    }

    private void replaceText(int start, int end, String replacement) {
        if (start < 0)
            start = 0;
        if (end > text.length())
            end = text.length();
        if (start > end)
            start = end;

        text = text.substring(0, start) + replacement + text.substring(end);
        cursorPosition = start + replacement.length();
        clearSelection();
        lastInputTime = System.currentTimeMillis();
    }

    private boolean hasSelection() {
        return selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd;
    }

    private String getSelectedText() {
        return text.substring(getStartOfSelection(), getEndOfSelection());
    }

    private int getStartOfSelection() {
        return Math.min(selectionStart, selectionEnd);
    }

    private int getEndOfSelection() {
        return Math.max(selectionStart, selectionEnd);
    }

    private void clearSelection() {
        selectionStart = -1;
        selectionEnd = -1;
    }

    private int getCursorIndexAt(double mouseX) {
        FontRenderer font = Fonts.getSize(12, Fonts.Type.BOLD);
        float relativeX = (float) mouseX - rectX - 3 + xOffset;
        int position = 0;
        while (position < text.length()) {
            float textWidth = font.getStringWidth(text.substring(0, position + 1));
            if (textWidth > relativeX) {
                break;
            }
            position++;
        }
        return position;
    }

    private void updateXOffset(FontRenderer font, int cursorPosition) {
        float cursorX = font.getStringWidth(text.substring(0, cursorPosition));
        if (cursorX < xOffset) {
            xOffset = cursorX;
        } else if (cursorX - xOffset > rectWidth - 17) {
            xOffset = cursorX - (rectWidth - 17);
        }
    }

    private void deleteSelectedText() {
        if (hasSelection()) {
            replaceText(getStartOfSelection(), getEndOfSelection(), "");
        }
    }
}
