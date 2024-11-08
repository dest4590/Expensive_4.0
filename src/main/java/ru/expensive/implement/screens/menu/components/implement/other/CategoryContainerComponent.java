package ru.expensive.implement.screens.menu.components.implement.other;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.common.util.player.MovingUtil;
import ru.expensive.implement.screens.menu.MenuScreen;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.category.CategoryComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.TextComponent;

import java.util.ArrayList;
import java.util.List;

@Setter
@Accessors(chain = true)
public class CategoryContainerComponent extends AbstractComponent {
    private final List<CategoryComponent> categoryComponents = new ArrayList<>();
    private MenuScreen menuScreen;

    public void initializeCategoryComponents() {
        categoryComponents.clear();
        for (ModuleCategory category : ModuleCategory.values()) {
            categoryComponents.add(new CategoryComponent(category, menuScreen));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float offset = 0;

        for (CategoryComponent component : categoryComponents) {
            component.x = x + 6;
            component.y = y + 50 + offset;
            component.width = 73;
            component.height = 17;
            component.render(context, mouseX, mouseY, delta);
            offset += component.height + 2;
        }
    }

    @Override
    public void tick() {
        boolean typing = TextComponent.typing;
        if (!typing) {
            for (KeyBinding keyBinding : MovingUtil.getMovementKeys(false)) {
                long handle = mc.getWindow().getHandle();
                int keyCode = keyBinding.getDefaultKey().getCode();
                keyBinding.setPressed(InputUtil.isKeyPressed(handle, keyCode));
            }
        } else {
            mc.options.jumpKey.setPressed(false);
            mc.options.forwardKey.setPressed(false);
            mc.options.rightKey.setPressed(false);
            mc.options.leftKey.setPressed(false);
            mc.options.backKey.setPressed(false);
        }

        for (CategoryComponent component : categoryComponents) {
            component.tick();
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        categoryComponents
                .forEach(categoryComponent -> categoryComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.mouseScrolled(mouseX, mouseY, amount));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        categoryComponents.forEach(categoryComponent -> categoryComponent.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }
}
