package ru.expensive.api.system.draw;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import ru.expensive.common.QuickImports;

import static net.minecraft.client.render.VertexFormat.DrawMode.QUADS;
import static net.minecraft.client.render.VertexFormats.POSITION;
import static net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DrawEngineImpl implements DrawEngine, QuickImports {

    @Override
    public void quad(Matrix4f matrix4f, float x, float y, float width, float height) {
        buffer.begin(QUADS, POSITION);
        {
            buffer.vertex(matrix4f, x, y, 0).next();
            buffer.vertex(matrix4f, x, y + height, 0).next();
            buffer.vertex(matrix4f, x + width, y + height, 0).next();
            buffer.vertex(matrix4f, x + width, y, 0).next();
        }
    }

    @Override
    public void quad(Matrix4f matrix4f, float x, float y, float width, float height, int color) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        buffer.begin(QUADS, POSITION_TEXTURE_COLOR);
        {
            buffer.vertex(matrix4f, x, y + height, 0).texture(0, 0).color(color).next();
            buffer.vertex(matrix4f, x + width, y + height, 0).texture(0, 1).color(color).next();
            buffer.vertex(matrix4f, x + width, y, 0).texture(1, 1).color(color).next();
            buffer.vertex(matrix4f, x, y, 0).texture(1, 0).color(color).next();
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());
    }
}
