package ru.expensive.implement.features.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.ColorSetting;
import ru.expensive.api.system.font.Fonts;
import ru.expensive.api.system.shape.ShapeProperties;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.math.ProjectionUtil;
import ru.expensive.common.util.render.VertexUtil;
import ru.expensive.implement.events.render.DrawEvent;
import ru.expensive.implement.events.render.WorldRenderEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static ru.expensive.implement.features.modules.render.AuctionHelperModule.RED_GREEN_COLORS;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PearlPredictionModule extends Module {

    final List<PearlPoint> pearlPoints = new ArrayList<>();

    ColorSetting lineColor = new ColorSetting("Line Color", "Color of the pearl prediction line.")
            .setColor(0xFF00FF00)
            .presets(RED_GREEN_COLORS);

    public PearlPredictionModule() {
        super("PearlPrediction", "Pearl Prediction", ModuleCategory.RENDER);
        setup(lineColor);
    }

    @EventHandler
    public void onDraw(DrawEvent drawEvent) {
        DrawContext context = drawEvent.getDrawContext();
        MatrixStack stack = context.getMatrices();

        for (PearlPoint pearlPoint : pearlPoints) {
            Vec3d pos = pearlPoint.position;
            Vector2f projection = ProjectionUtil.project(pos.x, pos.y - 0.3F, pos.z);
            int ticks = pearlPoint.ticks;

            if (projection.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) {
                continue;
            }

            double time = ticks * 50 / 1000.0;
            String text = String.format("%.1f", time);

            float textWidth = Fonts.getSize(13).getStringWidth(text) + 11;

            float posX = projection.getX() + textWidth / 2 - 6;
            float posY = projection.getY() - 8;

            float padding = 2;

            Matrix4f positionMatrix = stack.peek().getPositionMatrix();
            rectangle.render(ShapeProperties
                    .create(positionMatrix, posX - padding, posY - padding, padding + textWidth + padding, 10)
                    .round(4)
                    .thickness(2)
                    .softness(1)
                    .outlineColor(0xFF060712)
                    .color(0xB2060712)
                    .build());

            Image image = QuickImports.image.setMatrixStack(context.getMatrices());
            image.setTexture("textures/clock.png")
                    .render(ShapeProperties.create(positionMatrix, posX + textWidth - 7.5, posY - 0.5, 7, 7)
                            .build());

            Fonts.getSize(13).drawString(stack, text, posX + 1.2, posY + 1.4, -1);

            rectangle.render(ShapeProperties.create(positionMatrix, posX - padding, posY - 13, 10, 10)
                    .round(4)
                    .thickness(2)
                    .softness(1)
                    .outlineColor(0xFF060712)
                    .color(0xA2060712)
                    .build());

            stack.push();
            stack.translate(posX - 1, posY - 9.5, 0);
            stack.scale(0.5F, 0.5F, 0);
            context.drawItem(new ItemStack(Items.ENDER_PEARL), 0, -5);
            stack.translate(-posX - 1, -posY - 9.5, 0);
            stack.pop();
        }
    }

    @EventHandler
    public void onWorld(WorldRenderEvent worldRenderEvent) {
        MatrixStack stack = worldRenderEvent.getStack();
        Vec3d cameraPos = mc.getEntityRenderDispatcher().camera.getPos();
        stack.push();
        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        RenderSystem.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        RenderSystem.lineWidth(2);

        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        pearlPoints.clear();
        int lineColorValue = lineColor.getColor();
        Color lineColor = new Color(lineColorValue, true);

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EnderPearlEntity enderPearlEntity) {
                Vec3d motion = enderPearlEntity.getVelocity();
                Vec3d pos = enderPearlEntity.getPos();
                Vec3d prevPos;
                int ticks = 0;

                for (int i = 0; i < 150; i++) {
                    prevPos = pos;
                    pos = pos.add(motion);

                    motion = getNextMotion(enderPearlEntity, prevPos, motion);

                    HitResult hitResult = mc.world.raycast(
                            new RaycastContext(prevPos, pos,
                                    RaycastContext.ShapeType.COLLIDER,
                                    RaycastContext.FluidHandling.NONE,
                                    enderPearlEntity));

                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        pos = hitResult.getPos();
                    }

                    float alpha = i / 25.0f;
                    Color color = new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(),
                            MathHelper.clamp((int) (255 * alpha), 0, 255));

                    VertexUtil.vertexLine(stack, buffer,
                            (float) prevPos.x,
                            (float) prevPos.y,
                            (float) prevPos.z,
                            (float) pos.x,
                            (float) pos.y,
                            (float) pos.z,
                            color);

                    if (hitResult.getType() == HitResult.Type.BLOCK || pos.y < -128) {
                        pearlPoints.add(new PearlPoint(pos, ticks));
                        break;
                    }
                    ticks++;
                }
            }
        }

        tessellator.draw();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        stack.pop();
    }

    private Vec3d getNextMotion(ThrownEntity throwable, Vec3d prevPos, Vec3d motion) {
        boolean isInWater = mc.world.getBlockState(BlockPos.ofFloored(prevPos))
                .getFluidState()
                .isIn(FluidTags.WATER);

        if (isInWater) {
            motion = motion.multiply(0.8);
        } else {
            motion = motion.multiply(0.99);
        }

        if (!throwable.hasNoGravity()) {
            motion = motion.add(0, -0.03F, 0);
        }

        return motion;
    }

    record PearlPoint(Vec3d position, int ticks) {
    }
}
