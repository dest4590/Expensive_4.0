package ru.expensive.implement.screens.menu.components.implement.window.implement.module;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import ru.expensive.api.feature.module.setting.Setting;
import ru.expensive.api.feature.module.setting.SettingComponentAdder;
import ru.expensive.api.feature.module.setting.SettingRepository;
import ru.expensive.api.feature.module.setting.implement.BooleanSetting;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;
import ru.expensive.api.feature.module.setting.implement.TextSetting;
import ru.expensive.api.system.font.FontRenderer;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.core.Expensive;
import ru.expensive.core.client.ClientInfoProvider;
import ru.expensive.implement.screens.menu.components.AbstractComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.AbstractSettingComponent;
import ru.expensive.implement.screens.menu.components.implement.window.AbstractWindow;

import java.util.ArrayList;
import java.util.List;

public class InfoWindow extends AbstractWindow {
    public static final BooleanSetting booleanSetting = new BooleanSetting("Something", "Enables something in menu.")
            .setValue(false);
    public static final TextSetting extSetting = new TextSetting("Something", "Specify something.")
            .setText("protect");
    public static final SelectSetting selectSetting = new SelectSetting("AntiCheat", "Select mode.")
            .value("ReallyWorld", "FunTime", "HolyWorld Classic", "HolyWorld Lite", "AresMine");
    public static SettingRepository settingRepository = new SettingRepository();
    private final List<AbstractSettingComponent> components = new ArrayList<>();

    public InfoWindow() {
        settingRepository.setup(booleanSetting, extSetting, selectSetting);

        new SettingComponentAdder().addSettingComponent(
                settingRepository.settings(),
                components);
    }

    @Override
    public void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        // Короче зафиксишь для теста сделано
        List<Setting> settings = settingRepository.settings();
        // Мистер и
        if (settings.size() > 3) {
            settings.subList(3, settings.size()).clear();
        }

        // Мисс костыль ЕБАНЫЙ РОТ НАХУЙ БАБКУ ТВОЮ ШЛЮШКУ КРУТИЛ НА СПИНЕРЕ ЙОУ
        if (components.size() > 3) {
            components.subList(3, components.size()).clear();
        }

        final int TEXT_COLOR = 0xFF878894;
        final int WHITE_COLOR = 0xFFD4D6E1;
        final int PADDING_X = 13;
        final int PADDING_Y = 16;
        final int SPACING_Y = 13;
        final int FONT_SIZE = 14;
        final int SMALL_LOGO_WIDTH = 58;
        final int SMALL_LOGO_HEIGHT = 11;
        final int INFO_START_Y = 42;

        MatrixStack matrices = context.getMatrices();
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        renderImage("textures/about.png",
                positionMatrix,
                x,
                y,
                width,
                height);

        renderImage("textures/small_logo.png",
                positionMatrix,
                x + PADDING_X,
                y + PADDING_Y,
                SMALL_LOGO_WIDTH,
                SMALL_LOGO_HEIGHT);

        ClientInfoProvider clientInfoProvider = Expensive.getInstance().getClientInfoProvider();
        FontRenderer font = Fonts.getSize(FONT_SIZE);
        FontRenderer fontSemi = Fonts.getSize(FONT_SIZE, Fonts.Type.BOLD);
        float yOffset = y + INFO_START_Y;

        drawText(matrices, fontSemi, font, "Username: ", "dedinside", x + PADDING_X, yOffset, TEXT_COLOR, WHITE_COLOR);
        yOffset += SPACING_Y;
        drawText(matrices, fontSemi, font, "Verison: ", clientInfoProvider.clientVersion(), x + PADDING_X, yOffset,
                TEXT_COLOR, WHITE_COLOR);
        yOffset += SPACING_Y;
        drawText(matrices, fontSemi, font, "Branch: ", clientInfoProvider.clientBranch(), x + PADDING_X, yOffset,
                TEXT_COLOR, WHITE_COLOR);
        yOffset += SPACING_Y;
        drawText(matrices, fontSemi, font, "Updated: ", "22.05.2024", x + PADDING_X, yOffset, TEXT_COLOR, WHITE_COLOR);
        yOffset += SPACING_Y;
        drawText(matrices, fontSemi, font, "Valid until: ", "22.05.2034", x + PADDING_X, yOffset, TEXT_COLOR,
                WHITE_COLOR);

        float offset = y + 106;
        for (int i = components.size() - 1; i >= 0; i--) {
            AbstractSettingComponent component = components.get(i);

            component.x = x + 4;
            component.y = offset + (getComponentHeight() - component.height);
            component.width = 130;
            component.render(context, mouseX, mouseY, delta);

            offset -= component.height;
        }
    }

    @Override
    public void tick() {
        for (AbstractSettingComponent component : components) {
            component.tick();
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        draggable(MathUtil.isHovered(mouseX, mouseY, x, y, width, 40));

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

        for (AbstractSettingComponent abstractComponent : components) {
            if (abstractComponent.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return super.isHover(mouseX, mouseY);
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

    private void renderImage(String texturePath, Matrix4f positionMatrix, float x, float y, float width, float height) {
        image.setTexture(texturePath).render(
                ShapeProperties.create(positionMatrix, x, y, width, height)
                        .build());
    }

    private void drawText(MatrixStack matrices, FontRenderer fontSemi, FontRenderer font, String label, String value,
                          float x, float y, int labelColor, int valueColor) {
        fontSemi.drawString(matrices, label, x, y, labelColor);
        font.drawString(matrices, value, x + fontSemi.getStringWidth(label), y, valueColor);
    }

    public int getComponentHeight() {
        float offsetY = 0;
        for (AbstractComponent component : components) {
            offsetY += component.height;
        }
        return (int) (offsetY);
    }
}
