package ru.expensive.implement.screens.menu.components.implement.window.implement.settings.group;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.SettingComponentAdder;
import ru.expensive.api.feature.module.setting.implement.GroupSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.render.ScissorManager;
import ru.expensive.core.Expensive;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.AbstractSettingComponent;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GroupWindow extends AbstractWindow {
    private final List<AbstractSettingComponent> components = new ArrayList<>();
    private final GroupSetting setting;

    public GroupWindow(GroupSetting setting) {
        this.setting = setting;

        new SettingComponentAdder().addSettingComponent(
                setting.getSubSettings(),
                components);
    }

    @Override
    public void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        ScissorManager scissorManager = Expensive.getInstance()
                .getScissorManager();

        height = MathHelper.clamp(getComponentHeight(), 0, 200);

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

        Fonts.getSize(15, Fonts.Type.BOLD).drawString(context.getMatrices(), "Settings " + setting.getName(), x + 9,
                y + 10, -1);

        boolean isLimitedHeight = MathHelper.clamp(height, 0, 200) == 200;
        if (isLimitedHeight) {
            scissorManager.push(x, y + 23, width, height - 28);
        }

        float offset = 0;
        int totalHeight = 0;
        for (int i = components.size() - 1; i >= 0; i--) {
            AbstractSettingComponent component = components.get(i);

            var visible = component.getSetting()
                    .getVisible();

            if (visible != null && !visible.get()) {
                continue;
            }

            component.x = x;
            component.y = (float) (y + 19 + offset + (getComponentHeight() - 25 - component.height) + smoothedScroll);
            component.width = width;
            component.render(context, mouseX, mouseY, delta);

            offset -= component.height;
            totalHeight += (int) component.height;
        }

        if (isLimitedHeight) {
            scissorManager.pop();
        }

        int maxScroll = (int) Math.max(0, totalHeight - (height - 23));
        int clamped = MathHelper.clamp(maxScroll, 0, maxScroll);
        scroll = MathHelper.clamp(scroll, -clamped, 0);
        smoothedScroll = MathHelper.lerp(0.1F, smoothedScroll, scroll);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        draggable(MathUtil.isHovered(mouseX, mouseY, x, y, width, 19) && button == 0);

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

        components.forEach(abstractComponent -> abstractComponent.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        components.forEach(abstractComponent -> abstractComponent.isHover(mouseX, mouseY));

        for (AbstractComponent abstractComponent : components) {
            if (abstractComponent.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return super.isHover(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        components.forEach(abstractComponent -> abstractComponent.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        boolean scrolled = MathHelper.clamp(height, 0, 200) == 200
                && MathUtil.isHovered(mouseX, mouseY, x, y, width, height);
        if (scrolled) {
            scroll += amount * 20;
        }
        components.forEach(abstractComponent -> abstractComponent.mouseScrolled(mouseX, mouseY, amount));
        return scrolled;
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
        return (int) (offsetY + 25);
    }
}