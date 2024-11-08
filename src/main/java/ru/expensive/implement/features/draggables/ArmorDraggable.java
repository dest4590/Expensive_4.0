package ru.expensive.implement.features.draggables;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Expensive;

public class ArmorDraggable extends AbstractDraggable {
    private DefaultedList<ItemStack> armor;

    public ArmorDraggable() {
        super("Armor", 220, 10, 14, 10);
    }

    @Override
    public boolean visible() {
        return Expensive.getInstance().getModuleProvider()
                .module("Hud")
                .isState() && (!armor.isEmpty() || mc.currentScreen instanceof ChatScreen);
    }

    @Override
    public void tick(float delta) {
        armor = mc.player.getInventory().armor;
        super.tick(delta);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MatrixStack stack = context
                .getMatrices();

        Matrix4f positionMatrix = stack
                .peek()
                .getPositionMatrix();

        rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                .round(6)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, getX() + 10.5, getY() + 3, 0.8, 4)
                .color(0xFF2D2E41)
                .build());

        Image image = QuickImports.image.setMatrixStack(stack);
        image.setTexture("textures/shield.png")
                .render(ShapeProperties.create(positionMatrix, getX() + 3, getY() + 2, 6, 6)
                        .build());

        int offset = 14;
        for (ItemStack itemStack : armor) {
            if (itemStack.isEmpty())
                continue;

            stack.push();
            stack.translate(getX() + offset, getY() + 1, 0);
            stack.scale(0.5F, 0.5F, 0);
            RenderSystem.setShaderGlintAlpha(0);
            context.drawItem(itemStack, 0, 0);
            context.drawItemInSlot(mc.textRenderer, itemStack, 0, 0);
            RenderSystem.setShaderGlintAlpha(1);

            stack.translate(-(getX() + offset), -getY() + 1, 0);
            stack.pop();
            offset += 10;
        }
        setWidth(offset);
    }
}
