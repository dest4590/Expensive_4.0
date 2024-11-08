package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Expensive;

import java.util.List;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class HotKeysDraggable extends AbstractDraggable {
    private List<Module> key;

    public HotKeysDraggable() {
        super("HotKeys", 420, 10, 80, 18);
    }

    @Override
    public boolean visible() {
        return Expensive.getInstance().getModuleProvider()
                .module("Hud")
                .isState() && (!key.isEmpty() || mc.currentScreen instanceof ChatScreen);
    }

    @Override
    public void tick(float delta) {
        key = Expensive.getInstance().getModuleProvider()
                .getModules()
                .stream()
                .filter(module -> module.isState() && module.getKey() != -1
                        && module.getCategory() != ModuleCategory.RENDER)
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

        image.setTexture("textures/keyboard.png")
                .render(ShapeProperties
                        .create(positionMatrix, getX() + getWidth() - 16, getY() + 5, 8, 8)
                        .build());

        Fonts.getSize(13, BOLD).drawString(context.getMatrices(), getName(), getX() + 8, getY() + 7,
                0xFFD4D6E1);

        int offset = getY() + 21;
        for (Module module : key) {
            Fonts.getSize(11).drawString(context.getMatrices(), module.getName(), getX() + 8, offset,
                    0xFFD4D6E1);

            rectangle.render(ShapeProperties
                    .create(positionMatrix, getX() + getWidth() - 16, offset - 1, 8, 5)
                    .round(5)
                    .softness(1)
                    .thickness(2)
                    .outlineColor(-1)
                    .color(0x00FFFFFF)
                    .build());

            rectangle.render(ShapeProperties
                    .create(positionMatrix, getX() + getWidth() - 11.5, offset + 0.5, 2, 2)
                    .round(2)
                    .build());
            offset += 10;
        }

        setHeight(20 + key.size() * 10);
    }
}
