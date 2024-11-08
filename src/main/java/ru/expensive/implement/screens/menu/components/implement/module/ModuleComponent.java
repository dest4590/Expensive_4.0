package ru.expensive.implement.screens.menu.components.implement.module;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.setting.SettingComponentAdder;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.other.CheckComponent;
import ru.expensive.implement.screens.menu.components.implement.other.SettingComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.AbstractSettingComponent;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;
import ru.expensive.implement.screens.menu.components.implement.window.implement.module.ModuleBindWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;
import static ru.expensive.api.system.font.Fonts.Type.DEFAULT;

@Getter
public class ModuleComponent extends AbstractComponent {
    private final List<AbstractSettingComponent> components = new ArrayList<>();

    private final CheckComponent checkComponent = new CheckComponent();
    private final SettingComponent settingComponent = new SettingComponent();

    private final Module module;

    public ModuleComponent(Module module) {
        this.module = module;

        new SettingComponentAdder().addSettingComponent(
                module.settings(),
                components);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        height = getComponentHeight();

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, 18)
                .round(12, 0, 12, 0)
                .color(0xFF191a28)
                .build());

        rectangle.render(ShapeProperties.create(positionMatrix, x, y, width, height)
                .round(12)
                .softness(1)
                .thickness(2.2F)
                .outlineColor(0x902d2e41)
                .color(0x002d2e41)
                .build());

        image.setMatrixStack(context.getMatrices())
                .setTexture("textures/ico.png")
                .render(ShapeProperties.create(positionMatrix, x + 9, y + 4.5F, 9, 9)
                        .build());

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), module.getVisibleName(), x + 23, y + 7, 0xFFD4D6E1);

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), "Enable", x + 9, y + 27, 0xFFD4D6E1);
        Fonts.getSize(12, DEFAULT).drawString(context.getMatrices(),
                "Enables the " + module.getVisibleName().toLowerCase() + " feature.", x + 9, y + 36, 0xFF878894);

        ((CheckComponent) checkComponent.position(x + width - 16, y + 28.5F))
                .setRunnable(module::switchState)
                .setState(module.isState())
                .render(context, mouseX, mouseY, delta);

        ((SettingComponent) settingComponent.position(x + width - 28, y + 28.5F))
                .setRunnable(() -> spawnWindow(mouseX, mouseY))
                .render(context, mouseX, mouseY, delta);

        drawBind(context, positionMatrix);

        float offset = y + 42;
        for (int i = components.size() - 1; i >= 0; i--) {
            AbstractSettingComponent component = components.get(i);

            var visible = component.getSetting()
                    .getVisible();

            if (visible != null && !visible.get()) {
                continue;
            }

            component.x = x;
            component.y = offset + (getComponentHeight() - 46 - component.height);
            component.width = width;

            component.render(context, mouseX, mouseY, delta);

            offset -= component.height;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isAnyComponentHovered = components
                .stream()
                .anyMatch(abstractComponent -> abstractComponent.isHover(mouseX, mouseY));

        if (isAnyComponentHovered) {
            components.forEach(abstractComponent -> {
                if (abstractComponent.isHover(mouseX, mouseY)) {
                    abstractComponent.mouseClicked(mouseX, mouseY, button);
                }
            });
            return super.mouseClicked(mouseX, mouseY, button);
        }

        checkComponent.mouseClicked(mouseX, mouseY, button);
        settingComponent.mouseClicked(mouseX, mouseY, button);

        components.forEach(abstractComponent -> abstractComponent.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        for (AbstractComponent abstractComponent : components) {
            if (abstractComponent.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return MathUtil.isHovered(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public void tick() {
        for (AbstractComponent component : components) {
            component.tick();
        }
        super.tick();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        components.forEach(abstractComponent -> abstractComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        components.forEach(abstractComponent -> abstractComponent.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        components.forEach(abstractComponent -> abstractComponent.mouseScrolled(mouseX, mouseY, amount));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        components.forEach(abstractComponent -> abstractComponent.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        components.forEach(abstractComponent -> abstractComponent.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }

    public int getComponentHeight() {
        float offsetY = 0;
        for (AbstractSettingComponent component : components) {
            var visible = component.getSetting()
                    .getVisible();

            if (visible != null && !visible.get()) {
                continue;
            }

            offsetY += component.height;
        }
        return (int) (offsetY + 46);
    }

    private void drawBind(DrawContext context, Matrix4f positionMatrix) {
        String bindName = StringUtil.getBindName(module.getKey());
        float stringWidth = Fonts.getSize(12, BOLD).getStringWidth(bindName);

        rectangle.render(
                ShapeProperties.create(positionMatrix, x + width - stringWidth - 15, y + 4.5F, stringWidth + 6, 9)
                        .round(4)
                        .thickness(1)
                        .softness(1)
                        .outlineColor(0xFF282932)
                        .color(0xFF161725)
                        .build());

        int bindingColor = ColorHelper.Argb.getArgb(255, 135, 136, 148);
        Fonts.getSize(12, BOLD).drawString(context.getMatrices(), bindName, x + width - 12 - stringWidth, y + 8,
                bindingColor);
    }

    private void spawnWindow(int mouseX, int mouseY) {
        AbstractWindow existingWindow = null;

        for (AbstractWindow window : windowManager.getWindows()) {
            if (window instanceof ModuleBindWindow) {
                existingWindow = window;
                break;
            }
        }

        if (existingWindow != null) {
            windowManager.delete(existingWindow);
        } else {
            AbstractWindow moduleBindWindow = new ModuleBindWindow(module)
                    .position(mouseX + 5, mouseY + 5)
                    .size(105, 55)
                    .draggable(false);

            windowManager.add(moduleBindWindow);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ModuleComponent that = (ModuleComponent) o;
        return module.equals(that.module);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module);
    }
}
