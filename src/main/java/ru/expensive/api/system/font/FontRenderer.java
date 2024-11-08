package ru.expensive.api.system.font;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import ru.expensive.api.system.font.entry.DrawEntry;
import ru.expensive.api.system.font.glyph.Glyph;
import ru.expensive.api.system.font.glyph.GlyphMap;
import ru.expensive.common.QuickImports;

import java.awt.*;

import static net.minecraft.client.render.VertexFormat.DrawMode.QUADS;
import static net.minecraft.client.render.VertexFormats.POSITION_TEXTURE_COLOR;
import static ru.expensive.common.util.math.MathUtil.floorNearestMulN;
import static ru.expensive.common.util.other.StringUtil.randomString;

@Setter
@Accessors(chain = true)
public class FontRenderer implements QuickImports {
    private final Object2ObjectMap<Identifier, ObjectList<DrawEntry>> GLYPH_PAGE_CACHE = new Object2ObjectOpenHashMap<>();
    private final ObjectList<GlyphMap> maps = new ObjectArrayList<>();
    private Font font;

    public FontRenderer(Font font, float sizePx) {
        init(font, sizePx);
    }

    private static boolean visitForwards(String text, CharacterVisitor visitor) {
        int length = text.length();

        for (int i = 0; i < length; ++i) {
            char c = text.charAt(i);
            if (!visitor.accept(i, c)) {
                return false;
            }
        }

        return true;
    }

    @Contract(value = "-> new", pure = true)
    public static @NotNull Identifier randomIdentifier() {
        return new Identifier("expensive", "temp/" + randomString(32));
    }

    private void init(Font font, float sizePx) {
        this.font = font.deriveFont(sizePx * 2);
    }

    private GlyphMap generateMap(char from, char to) {
        GlyphMap glyphMap = new GlyphMap(from, to, this.font, randomIdentifier(), 5);
        maps.add(glyphMap);
        return glyphMap;
    }

    private Glyph locateGlyph(char glyph) {
        for (GlyphMap map : maps) {
            if (map.contains(glyph)) {
                return map.getGlyph(glyph);
            }
        }

        char base = (char) floorNearestMulN(glyph, 256);
        return generateMap(base, (char) (base + 256))
                .getGlyph(glyph);
    }

    public void drawString(MatrixStack stack, String text, double x, double y, int color) {
        stack.push();
        y -= 3f;
        stack.translate(x, y, 0);
        stack.scale(0.5f, 0.5f, 0.5f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        Matrix4f matrix4f = stack
                .peek()
                .getPositionMatrix();

        char[] chars = text.toCharArray();

        float xOffset = 0;
        float yOffset = 0;
        int lineStart = 0;

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\n') {
                yOffset += getStringHeight(text.substring(lineStart, i)) - 2;
                xOffset = 0;
                lineStart = i + 1;
                continue;
            }
            Glyph glyph = locateGlyph(c);
            if (glyph != null) {
                if (glyph.value() != ' ') {
                    Identifier i1 = glyph.owner().bindToTexture;
                    DrawEntry entry = new DrawEntry(xOffset, yOffset, color, glyph);
                    GLYPH_PAGE_CACHE.computeIfAbsent(i1, integer -> new ObjectArrayList<>()).add(entry);
                }
                xOffset += glyph.width();
            }
        }

        drawGlyphs(matrix4f);

        GLYPH_PAGE_CACHE.clear();
        stack.pop();
    }

    public void drawGradientString(MatrixStack stack, String text, double x, double y, int colorStart, int colorEnd) {
        stack.push();
        y -= 3f;
        stack.translate(x, y, 0);
        stack.scale(0.5f, 0.5f, 0.5f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        Matrix4f matrix4f = stack.peek().getPositionMatrix();

        char[] chars = text.toCharArray();

        float xOffset = 0;
        float yOffset = 0;
        int lineStart = 0;
        int textLength = text.length();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '\n') {
                yOffset += getStringHeight(text.substring(lineStart, i)) - 2;
                xOffset = 0;
                lineStart = i + 1;
                continue;
            }
            Glyph glyph = locateGlyph(c);
            if (glyph != null) {
                if (glyph.value() != ' ') {
                    float t = (float) i / (textLength - 1);
                    int color = interpolateColor(colorStart, colorEnd, t);
                    Identifier i1 = glyph.owner().bindToTexture;
                    DrawEntry entry = new DrawEntry(xOffset, yOffset, color, glyph);
                    GLYPH_PAGE_CACHE.computeIfAbsent(i1, integer -> new ObjectArrayList<>()).add(entry);
                }
                xOffset += glyph.width();
            }
        }

        drawGlyphs(matrix4f);

        GLYPH_PAGE_CACHE.clear();
        stack.pop();
    }

    private int interpolateColor(int colorStart, int colorEnd, float t) {
        float startAlpha = (colorStart >> 24 & 255) / 255.0F;
        float startRed = (colorStart >> 16 & 255) / 255.0F;
        float startGreen = (colorStart >> 8 & 255) / 255.0F;
        float startBlue = (colorStart & 255) / 255.0F;

        float endAlpha = (colorEnd >> 24 & 255) / 255.0F;
        float endRed = (colorEnd >> 16 & 255) / 255.0F;
        float endGreen = (colorEnd >> 8 & 255) / 255.0F;
        float endBlue = (colorEnd & 255) / 255.0F;

        float alpha = startAlpha + t * (endAlpha - startAlpha);
        float red = startRed + t * (endRed - startRed);
        float green = startGreen + t * (endGreen - startGreen);
        float blue = startBlue + t * (endBlue - startBlue);

        return ((int) (alpha * 255.0F) << 24) | ((int) (red * 255.0F) << 16) | ((int) (green * 255.0F) << 8)
                | (int) (blue * 255.0F);
    }

    private void drawGlyphs(Matrix4f matrix) {
        for (Identifier identifier : GLYPH_PAGE_CACHE.keySet()) {
            RenderSystem.setShaderTexture(0, identifier);
            buffer.begin(QUADS, POSITION_TEXTURE_COLOR);
            {
                for (DrawEntry drawEntry : GLYPH_PAGE_CACHE.get(identifier)) {
                    float x = drawEntry.atX();
                    float y = drawEntry.atY();

                    Glyph glyph = drawEntry.toDraw();
                    GlyphMap glyphMap = glyph.owner();

                    float width = glyph.width();
                    float height = glyph.height();

                    float u1 = (float) glyph.u() / glyphMap.width;
                    float v1 = (float) glyph.v() / glyphMap.height;
                    float u2 = (float) (glyph.u() + glyph.width()) / glyphMap.width;
                    float v2 = (float) (glyph.v() + glyph.height()) / glyphMap.height;

                    int color = drawEntry.color();

                    buffer.vertex(matrix, x + 0, y + height, 0).texture(u1, v2).color(color).next();
                    buffer.vertex(matrix, x + width, y + height, 0).texture(u2, v2).color(color).next();
                    buffer.vertex(matrix, x + width, y + 0, 0).texture(u2, v1).color(color).next();
                    buffer.vertex(matrix, x + 0, y + 0, 0).texture(u1, v1).color(color).next();
                }
            }
            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
    }

    public void drawCenteredString(MatrixStack stack, String s, double x, double y, int color) {
        drawString(stack, s, (float) (x - getStringWidth(s) / 2f), (float) y, color);
    }

    public float getStringWidth(String text) {
        float currentLine = 0;
        float maxPreviousLines = 0;

        for (char c : text.toCharArray()) {
            if (c == '\n') {
                maxPreviousLines = Math.max(currentLine, maxPreviousLines);
                currentLine = 0;
                continue;
            }

            Glyph glyph = locateGlyph(c);
            currentLine += glyph == null ? 0 : glyph.width();
        }

        return Math.max(currentLine, maxPreviousLines) / 2;
    }

    public float getStringHeight(String text) {
        float currentLine = 0;
        float previous = 0;

        for (char c : (text.isEmpty() ? " " : text).toCharArray()) {
            if (c == '\n') {
                currentLine = (currentLine == 0 ? locateGlyph(' ').height() : currentLine);
                previous += currentLine;
                currentLine = 0;
                continue;
            }
            Glyph glyph = locateGlyph(c);
            currentLine = Math.max(glyph == null ? 0 : glyph.height(), currentLine);
        }

        return currentLine + previous;
    }

    public String trimToWidth(String text, int maxWidth, boolean backwards) {
        return backwards ? trimToWidthBackwards(text, maxWidth) : trimToWidth(text, maxWidth);
    }

    public String trimToWidth(String text, int maxWidth) {
        return text.substring(0, getTrimmedLength(text, maxWidth));
    }

    private int getTrimmedLength(String text, int maxWidth) {
        WidthLimitingVisitor visitor = new WidthLimitingVisitor(maxWidth);
        visitForwards(text, visitor);
        return visitor.getLength();
    }

    public String trimToWidthBackwards(String text, int maxWidth) {
        MutableFloat widthLeft = new MutableFloat(maxWidth);
        MutableInt trimmedLength = new MutableInt(text.length());

        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            Glyph glyph = locateGlyph(c);
            if (glyph != null) {
                widthLeft.subtract(glyph.width());
                if (widthLeft.floatValue() < 0) {
                    trimmedLength.setValue(i + 1);
                    break;
                }
            }
        }

        return text.substring(trimmedLength.intValue());
    }

    @FunctionalInterface
    public interface CharacterVisitor {
        boolean accept(int index, char codePoint);
    }

    private class WidthLimitingVisitor implements CharacterVisitor {
        private float widthLeft;
        @Getter
        private int length;

        public WidthLimitingVisitor(float maxWidth) {
            this.widthLeft = maxWidth;
        }

        @Override
        public boolean accept(int index, char c) {
            Glyph glyph = locateGlyph(c);
            if (glyph != null) {
                widthLeft -= glyph.width();
                if (widthLeft >= 0) {
                    length = index + 1;
                    return true;
                }
            }
            return false;
        }

    }
}
