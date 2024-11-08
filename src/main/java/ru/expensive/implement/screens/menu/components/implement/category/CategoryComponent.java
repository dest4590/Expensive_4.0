package ru.expensive.implement.screens.menu.components.implement.category;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.system.animation.Animation;
import ru.expensive.api.system.animation.Direction;
import ru.expensive.api.system.animation.implement.DecelerateAnimation;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.common.util.render.ScissorManager;
import ru.expensive.core.Expensive;
import ru.expensive.implement.screens.menu.MenuScreen;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.module.ModuleComponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryComponent extends AbstractComponent {
    private static final Set<ModuleComponent> globalModuleComponents = new HashSet<>();
    private final List<ModuleComponent> moduleComponents = new ArrayList<>();
    private final ModuleCategory category;
    private final MenuScreen menuScreen;

    private final Animation alphaAnimation = new DecelerateAnimation()
            .setMs(500)
            .setValue(255);

    public CategoryComponent(ModuleCategory category, MenuScreen menuScreen) {
        this.category = category;
        this.menuScreen = menuScreen;

        List<Module> modules = Expensive.getInstance()
                .getModuleRepository()
                .modules();

        for (Module module : modules) {
            ModuleComponent newComponent = new ModuleComponent(module);

            if (globalModuleComponents.add(newComponent)) {
                moduleComponents.add(newComponent);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        globalModuleComponents.clear();
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        ScissorManager scissorManager = Expensive.getInstance()
                .getScissorManager();

        drawCategoryTab(context, positionMatrix);

        int[] offsets = calculateOffsets();
        int columnWidth = 137;
        int column = 0;
        int maxScroll = 0;

        int menuScreenWidth = menuScreen.width;
        int menuScreenHeight = menuScreen.height - 30;
        int scaledMenuScreenWidth = (int) (menuScreenWidth * menuScreen.getScaleAnimation());
        int scaledMenuScreenHeight = (int) (menuScreenHeight * menuScreen.getScaleAnimation());

        scissorManager.push(menuScreen.x, menuScreen.y + 29, scaledMenuScreenWidth, scaledMenuScreenHeight);

        for (int i = moduleComponents.size() - 1; i >= 0; i--) {
            ModuleComponent component = moduleComponents.get(i);

            if (shouldRenderComponent(component)) {
                int componentHeight = component.getComponentHeight() + 9;

                component.x = menuScreen.x + 95 + (column * (columnWidth + 10));
                component.y = (float) (menuScreen.y + 39 + offsets[column] - componentHeight + smoothedScroll);
                component.width = columnWidth;

                component.render(context, mouseX, mouseY, delta);
                offsets[column] -= componentHeight;
                maxScroll = Math.max(maxScroll, offsets[column]);

                column = (column + 1) % 2;
            }
        }
        scissorManager.pop();

        int clamped = MathHelper.clamp(maxScroll - (menuScreen.height / 2 - 50), 0, maxScroll);
        scroll = MathHelper.clamp(scroll, -clamped, 0);
        smoothedScroll = MathHelper.lerp(0.1F, smoothedScroll, scroll);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height) && button == 0) {
            menuScreen.category = category;
        }

        if (MathUtil.isHovered(mouseX, mouseY, menuScreen.x, menuScreen.y + 29, menuScreen.width,
                menuScreen.height - 30)) {
            boolean isAnyComponentHovered = moduleComponents
                    .stream()
                    .anyMatch(moduleComponent -> moduleComponent.isHover(mouseX, mouseY));

            if (isAnyComponentHovered) {
                moduleComponents.forEach(moduleComponent -> {
                    if (shouldRenderComponent(moduleComponent)) {
                        if (moduleComponent.isHover(mouseX, mouseY)) {
                            moduleComponent.mouseClicked(mouseX, mouseY, button);
                        }
                    }
                });
                return super.mouseClicked(mouseX, mouseY, button);
            }

        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        moduleComponents.forEach(moduleComponent -> moduleComponent.isHover(mouseX, mouseY));

        for (ModuleComponent moduleComponent : moduleComponents) {
            if (moduleComponent.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return super.isHover(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        moduleComponents.forEach(moduleComponent -> moduleComponent.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (MathUtil.isHovered(mouseX, mouseY, menuScreen.x, menuScreen.y + 29, menuScreen.width,
                menuScreen.height - 30)) {
            scroll += amount * 20;
        }

        moduleComponents.forEach(moduleComponent -> {
            if (shouldRenderComponent(moduleComponent)) {
                moduleComponent.mouseScrolled(mouseX, mouseY, amount);
            }
        });
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        moduleComponents.forEach(moduleComponent -> {
            if (shouldRenderComponent(moduleComponent)) {
                moduleComponent.keyPressed(keyCode, scanCode, modifiers);
            }
        });
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        moduleComponents.forEach(moduleComponent -> {
            if (shouldRenderComponent(moduleComponent)) {
                moduleComponent.charTyped(chr, modifiers);
            }
        });
        return super.charTyped(chr, modifiers);
    }

    private void drawCategoryTab(DrawContext context, Matrix4f positionMatrix) {
        Image image = QuickImports.image.setMatrixStack(context.getMatrices());

        alphaAnimation.setDirection(menuScreen.category == category
                ? Direction.FORWARDS
                : Direction.BACKWARDS);

        alphaAnimation.setMs(500);

        image.setTexture("textures/tab.png").render(
                ShapeProperties.create(positionMatrix, x, y, width, height)
                        .color(MathUtil.applyOpacity(-1, alphaAnimation.getOutput().intValue()))
                        .build());

        String texture = "textures/"
                + category.getReadableName().toLowerCase()
                + ".png";

        image.setTexture(texture).render(
                ShapeProperties.create(positionMatrix, x + 7, y + 4, 9, 9)
                        .build());

        int selectColor = menuScreen.category == category
                ? 0xFF8187FF
                : 0xFFD4D6E1;

        Fonts.getSize(14, Fonts.Type.BOLD).drawString(context.getMatrices(), category.getReadableName(), x + 22,
                y + 6.3, selectColor);
    }

    private int[] calculateOffsets() {
        int[] offsets = new int[2];
        int column = 0;

        for (int i = moduleComponents.size() - 1; i >= 0; i--) {
            ModuleComponent component = moduleComponents.get(i);

            if (shouldRenderComponent(component)) {
                int componentHeight = component.getComponentHeight() + 9;
                offsets[column] += componentHeight;
                column = (column + 1) % 2;
            }
        }

        return offsets;
    }

    private boolean shouldRenderComponent(ModuleComponent component) {
        ModuleCategory moduleCategory = component.getModule().getCategory();

        String text = menuScreen.getSearchComponent()
                .getText()
                .toLowerCase();

        String moduleName = component.getModule()
                .getVisibleName()
                .toLowerCase();

        return text.equalsIgnoreCase("") ? moduleCategory == menuScreen.category : moduleName.contains(text);
    }
}
