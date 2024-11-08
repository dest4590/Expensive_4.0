package ru.expensive.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleProvider;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.render.ScissorManager;
import ru.expensive.core.Expensive;
import ru.expensive.implement.features.modules.combat.AuraModule;

import static ru.expensive.api.system.font.Fonts.Type.BOLD;

public class TargetHudDraggable extends AbstractDraggable {
    private LivingEntity currentTarget;
    private float health;

    public TargetHudDraggable() {
        super("TargetHud", 10, 10, 100, 40);
    }

    @Override
    public boolean visible() {
        ModuleProvider moduleProvider = Expensive.getInstance().getModuleProvider();
        Module aura = moduleProvider.module("Aura");
        AuraModule auraModule = (AuraModule) aura;

        return currentTarget != null
                && !(auraModule.getMaxDistanceSetting().getValue() <= mc.player.distanceTo(currentTarget))
                && moduleProvider.module("Hud").isState()
                && aura.isState();
    }

    @Override
    public void tick(float delta) {
        Module aura = Expensive.getInstance().getModuleProvider().module("Aura");
        AuraModule auraModule = (AuraModule) aura;

        if (auraModule.getTarget() != null) {
            currentTarget = auraModule.getTarget();
        }

        if (!aura.isState() || auraModule.getTarget() == null) {
            startCloseAnimation();
        } else {
            startAnimation();
        }

        super.tick(delta);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        Matrix4f positionMatrix = context
                .getMatrices()
                .peek()
                .getPositionMatrix();

        if (currentTarget != null) {
            health = MathHelper.clamp(MathHelper.lerp(0.05F, health, getHealth(61)), 0, 61);

            rectangle.render(ShapeProperties.create(positionMatrix, getX(), getY(), getWidth(), getHeight())
                    .round(12)
                    .softness(1)
                    .thickness(2)
                    .outlineColor(0xFF2D2E41)
                    .color(0xF2141724)
                    .build());

            ScissorManager scissorManager = Expensive.getInstance()
                    .getScissorManager();

            scissorManager.push(getX(), getY(), getWidth() - 5, getHeight());
            Fonts.getSize(18, BOLD).drawString(context.getMatrices(), currentTarget.getName().getString(), getX() + 34,
                    getY() + 10, -1);
            scissorManager.pop();

            Fonts.getSize(14, BOLD).drawString(context.getMatrices(), String.valueOf(getHealth(100)), getX() + 34,
                    getY() + 21, 0xFF8187FF);

            rectangle.render(ShapeProperties.create(positionMatrix, getX() + 34, getY() + 28.2F, 61, 2F)
                    .round(2)
                    .color(0xFF060712)
                    .build());

            rectangle.render(ShapeProperties.create(positionMatrix, getX() + 34, getY() + 28.2F, health, 2F)
                    .softness(20)
                    .round(6)
                    .color(0x188187FF)
                    .build());
            rectangle.render(ShapeProperties.create(positionMatrix, getX() + 34, getY() + 28.2F, health, 2F)
                    .round(2)
                    .color(0xFF8187FF)
                    .build());

            Image image = QuickImports.image.setMatrixStack(context.getMatrices());
            image.setTexture("textures/steve.png")
                    .render(ShapeProperties.create(positionMatrix, getX() + 5, getY() + 7.5F, 25, 25)
                            .build());

            image.setTexture("textures/health.png")
                    .render(ShapeProperties.create(positionMatrix, getX() + 88, getY() + 19, 7, 7)
                            .build());
        }
    }

    private int getHealth(int cent) {
        return Math.round(((currentTarget.getHealth() / currentTarget.getMaxHealth()) * cent));
    }
}
