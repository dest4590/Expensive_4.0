package ru.expensive.implement.screens.menu.components.implement.window.implement;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix4f;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;

import static ru.expensive.common.util.other.StringUtil.getBindName;

@RequiredArgsConstructor
public abstract class AbstractBindWindow extends AbstractWindow {
    private boolean binding;

    protected abstract int getKey();

    protected abstract void setKey(int key);

    protected abstract int getType();

    protected abstract void setType(int type);

    @Override
    public void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        // Window background
        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(12)
                .softness(25)
                .color(0x32000000)
                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(12)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xFF191A28)
                .build());

        Fonts.getSize(14).drawString(context.getMatrices(), "Binding module", x + 5, y + 8, -1);

        image.setMatrixStack(context.getMatrices())
                .setTexture("textures/trash.png")
                .render(ShapeProperties.create(positionMatrix, x + width - 13, y + 5.3f, 8, 8)
                        .build());

        drawKeyButton(context);
        drawTypeButton(context);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (MathUtil.isHovered(mouseX, mouseY, x + width - 57, y + 37F, 52, 13)) {
                setType(getType() != 1 ? 1 : 0);
            }

            float stringWidth = Fonts.getSize(14).getStringWidth(getBindName(getKey()));

            if (MathUtil.isHovered(mouseX, mouseY, x + width - stringWidth - 15, y + 18.8F, stringWidth + 10, 13)) {
                binding = !binding;
            }

            if (MathUtil.isHovered(mouseX, mouseY, x + width - 13, y + 5.3f, 8, 8)) {
                setKey(-1);
            }
        }

        if (binding && button > 1) {
            setKey(button);
            binding = false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            setKey(keyCode);
            binding = false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void drawKeyButton(DrawContext context) {
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();

        float stringWidth = Fonts.getSize(14).getStringWidth(getBindName(getKey()));

        rectangle.render(
                ShapeProperties.create(positionMatrix, x + width - stringWidth - 15, y + 18.8F, stringWidth + 10, 13)
                        .round(4)
                        .thickness(1)
                        .softness(1)
                        .outlineColor(0xFF282932)
                        .color(0xFF161725)
                        .build());

        int bindingColor = binding ? 0xFF8187FF : 0xFFD4D6E1;

        Fonts.getSize(14).drawString(context.getMatrices(), getBindName(getKey()), x + width - 10 - stringWidth,
                y + 23.6F, bindingColor);
        Fonts.getSize(14).drawString(context.getMatrices(), "Key", x + 5, y + 24.3, 0xFFD4D6E1);
    }

    private void drawTypeButton(DrawContext context) {
        Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();

        // background
        rectangle.render(ShapeProperties.create(positionMatrix, x + width - 57, y + 37F, 52, 13)
                .round(4)
                .thickness(1)
                .softness(1)
                .outlineColor(0xFF282932)
                .color(0xFF161725)
                .build());

        // select type
        if (getType() == 1) {
            rectangle.render(ShapeProperties.create(positionMatrix, x + width - 34, y + 37F, 29, 13)
                    .round(4, 4, 0, 0)
                    .color(0xFF8187FF)
                    .build());
        } else {
            rectangle.render(ShapeProperties.create(positionMatrix, x + width - 57, y + 37F, 23, 13)
                    .round(0, 0, 4, 4)
                    .color(0xFF8187FF)
                    .build());
        }

        Fonts.getSize(12).drawString(context.getMatrices(), "HOLD", x + 52, y + 42.3, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(context.getMatrices(), "TOGGLE", x + 73, y + 42.3, 0xFFD4D6E1);

        Fonts.getSize(14).drawString(context.getMatrices(), "Bind mode", x + 5, y + 42.3, 0xFFD4D6E1);
    }
}