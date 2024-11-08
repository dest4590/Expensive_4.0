package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Expensive;

import java.util.List;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class PotionsDraggable extends AbstractDraggable {
    private List<StatusEffectInstance> potions;

    public PotionsDraggable() {
        super("Potions", 520, 10, 92, 18);
    }

    @Override
    public boolean visible() {
        return Expensive.getInstance().getModuleProvider()
                .module("Hud")
                .isState() && (!potions.isEmpty() || mc.currentScreen instanceof ChatScreen);
    }

    @Override
    public void tick(float delta) {
        potions = mc.player.getStatusEffects()
                .stream()
                .filter(effect -> effect.getDuration() > 0)
                .toList();

        super.tick(delta);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                .round(12)
                .softness(1)
                .thickness(2)
                .outlineColor(0xFF2D2E41)
                .color(0xF2141724)
                .build());

        Image image = QuickImports.image.setMatrixStack(context.getMatrices());
        image.setTexture("textures/separator.png")
                .render(ShapeProperties.create(positionMatrix, getX(), getY() + 16, getWidth(), 1)
                        .build());

        image.setTexture("textures/potion.png")
                .render(ShapeProperties.create(positionMatrix, getX() + getWidth() - 16, getY() + 5, 7, 7)
                        .build());

        Fonts.getSize(13, BOLD).drawString(context.getMatrices(), getName(), getX() + 8, getY() + 7, 0xFFD4D6E1);

        int offset = getY() + 21;
        for (StatusEffectInstance effect : potions) {
            String name = effect.getEffectType().getName().getString();
            String duration = StatusEffectUtil.getDurationText(effect, 1).getString();
            int amplifier = effect.getAmplifier();

            Fonts.getSize(11).drawString(context.getMatrices(), name, getX() + 8, offset, 0xFFD4D6E1);
            Fonts.getSize(11).drawString(context.getMatrices(), duration,
                    getX() + getWidth() - 8 - Fonts.getSize(11).getStringWidth(duration), offset, 0xFFD4D6E1);

            if (amplifier > 0) {
                Fonts.getSize(11).drawString(context.getMatrices(), String.valueOf(amplifier), getX() + 55, offset,
                        amplifier == 1 ? 0xFFD4D6E1 : 0xFF8187FF);
            }
            offset += 10;
        }

        setHeight(20 + potions.size() * 10);
    }
}
