package ru.expensive.implement.screens.menu;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.other.*;
import ru.expensive.implement.screens.menu.components.implement.window.implement.module.InfoWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.expensive.api.system.animation.Direction.BACKWARDS;
import static ru.expensive.api.system.animation.Direction.FORWARDS;

public class MenuScreen extends Screen implements QuickImports {
    private final List<AbstractComponent> components = new ArrayList<>();

    private final BackgroundComponent backgroundComponent = new BackgroundComponent();
    private final UserComponent userComponent = new UserComponent();
    private final LanguageComponent languageComponent = new LanguageComponent();
    @Getter
    private final SearchComponent searchComponent = new SearchComponent();
    private final CategoryContainerComponent categoryContainerComponent = new CategoryContainerComponent();
    private final Animation scaleAnimation = new DecelerateAnimation()
            .setMs(150)
            .setValue(1);
    private final Animation alphaAnimation = new DecelerateAnimation()
            .setMs(400)
            .setValue(100);
    public int x, y, width, height;
    public ModuleCategory category = ModuleCategory.COMBAT;

    public MenuScreen() {
        super(Text.of("Delta"));

        scaleAnimation.setDirection(FORWARDS);
        alphaAnimation.setDirection(FORWARDS);

        categoryContainerComponent
                .setMenuScreen(this)
                .initializeCategoryComponents();

        components.addAll(
                Arrays.asList(
                        backgroundComponent,
                        userComponent,
                        languageComponent,
                        searchComponent,
                        categoryContainerComponent));
    }

    @Override
    public void tick() {
        close();
        components.forEach(AbstractComponent::tick);
        super.tick();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        x = window.getScaledWidth() / 2 - 200;
        y = window.getScaledHeight() / 2 - 125;
        width = 400;
        height = 250;

        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        int opacity = alphaAnimation
                .getOutput()
                .intValue();

        rectangle.render(ShapeProperties.create(positionMatrix, 0, 0, window.getScaledWidth(), window.getScaledHeight())
                .color(MathUtil.applyOpacity(0xFF000000, opacity))
                .build());

        backgroundComponent.setMenuScreen(this)
                .position(x, y)
                .size(width, height);

        userComponent.setMenuScreen(this)
                .position(x, y + height);

        languageComponent.position(x + 261, y + 6);
        searchComponent.position(x + 300, y + 6);
        categoryContainerComponent.position(x, y);

        MathUtil.scale(context.getMatrices(), x + (float) width / 2, y + (float) height / 2, getScaleAnimation(),
                () -> {
                    components.forEach(component -> component.render(context, mouseX, mouseY, delta));
                    windowManager.render(context, mouseX, mouseY, delta);
                });
        super.render(context, mouseX, mouseY, delta);
    }

    public float getScaleAnimation() {
        return scaleAnimation.getOutput().floatValue();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!windowManager.mouseClicked(mouseX, mouseY, button)) {
            components.forEach(component -> component.mouseClicked(mouseX, mouseY, button));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        components.forEach(component -> component.mouseReleased(mouseX, mouseY, button));
        windowManager.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!windowManager.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            components.forEach(component -> component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!windowManager.mouseScrolled(mouseX, mouseY, amount)) {
            components.forEach(component -> component.mouseScrolled(mouseX, mouseY, amount));
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && shouldCloseOnEsc()) {
            scaleAnimation.setDirection(BACKWARDS);
            alphaAnimation.setDirection(BACKWARDS);
            return true;
        }

        if (!windowManager.keyPressed(keyCode, scanCode, modifiers)) {
            components.forEach(component -> component.keyPressed(keyCode, scanCode, modifiers));
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!windowManager.charTyped(chr, modifiers)) {
            components.forEach(component -> component.charTyped(chr, modifiers));
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        if (scaleAnimation.isFinished(BACKWARDS)) {
            windowManager.getWindows().forEach(abstractWindow -> {
                if (!(abstractWindow instanceof InfoWindow)) {
                    windowManager.delete(abstractWindow);
                }
            });
            super.close();
        }
    }
}
