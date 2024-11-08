package ru.expensive.api.system.font.glyph;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import lombok.SneakyThrows;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.system.MemoryUtil;
import ru.expensive.asm.mixins.accessors.NativeImageAccessor;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.awt.RenderingHints.*;
import static net.minecraft.client.texture.NativeImage.Format.RGBA;

public class GlyphMap {
    public final Identifier bindToTexture;
    private final Char2ObjectArrayMap<Glyph> glyphs = new Char2ObjectArrayMap<>();
    private final char fromIncl, toExcl;
    private final Font font;
    private final int pixelPadding;
    public int width, height;
    private boolean generated = false;

    public GlyphMap(char from, char to, Font font, Identifier identifier, int padding) {
        this.fromIncl = from;
        this.toExcl = to;
        this.font = font;
        this.bindToTexture = identifier;
        this.pixelPadding = padding;
    }

    @SneakyThrows
    public static void registerBufferedImageTexture(Identifier textureIdentifier, BufferedImage inputImage) {
        int imageWidth = inputImage.getWidth();
        int imageHeight = inputImage.getHeight();

        NativeImage nativeImage = new NativeImage(RGBA, imageWidth, imageHeight, false);

        @SuppressWarnings("DataFlowIssue")
        long pointer = ((NativeImageAccessor) (Object) nativeImage).getPointer();

        IntBuffer buffer = MemoryUtil.memIntBuffer(pointer, nativeImage.getWidth() * nativeImage.getHeight());

        WritableRaster raster = inputImage.getRaster();
        ColorModel colorModel = inputImage.getColorModel();

        Object data = createDataArrayBasedOnRaster(raster);

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                raster.getDataElements(x, y, data);
                int alpha = colorModel.getAlpha(data);
                int red = colorModel.getRed(data);
                int green = colorModel.getGreen(data);
                int blue = colorModel.getBlue(data);
                buffer.put(ColorHelper.Abgr.getAbgr(alpha, blue, green, red));
            }
        }

        NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
        texture.upload();

        if (RenderSystem.isOnRenderThread()) {
            MinecraftClient.getInstance().getTextureManager().registerTexture(textureIdentifier, texture);
        } else {
            RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().getTextureManager()
                    .registerTexture(textureIdentifier, texture));
        }
    }

    private static Object createDataArrayBasedOnRaster(WritableRaster raster) {
        return switch (raster.getDataBuffer().getDataType()) {
            case DataBuffer.TYPE_BYTE -> new byte[raster.getNumDataElements()];
            case DataBuffer.TYPE_USHORT -> new short[raster.getNumDataElements()];
            case DataBuffer.TYPE_INT -> new int[raster.getNumDataElements()];
            default -> throw new IllegalArgumentException(
                    "Unsupported data buffer type: " + raster.getDataBuffer().getDataType());
        };
    }

    public Glyph getGlyph(char c) {
        if (!generated)
            generate();
        return glyphs.get(c);
    }

    public boolean contains(char c) {
        return c >= fromIncl && c < toExcl;
    }

    private Font getFontForGlyph(char c) {
        return font.canDisplay(c) ? this.font : new Font("SansSerif", 0, font.getSize());
    }

    public void generate() {
        if (generated)
            return;

        int range = toExcl - fromIncl - 1;
        int charsVert = (int) (Math.ceil(Math.sqrt(range)) * 1.5);
        glyphs.clear();

        int generatedChars = 0;
        int charNX = 0;
        int maxX = 0, maxY = 0;
        int currentX = 0, currentY = 0;
        int currentRowMaxY = 0;

        List<Glyph> glyphs1 = new ArrayList<>();
        AffineTransform affineTransform = new AffineTransform();
        FontRenderContext fontRenderContext = new FontRenderContext(affineTransform, true, false);

        while (generatedChars <= range) {
            char currentChar = (char) (fromIncl + generatedChars);
            Font font = getFontForGlyph(currentChar);

            Rectangle2D stringBounds = font.getStringBounds(String.valueOf(currentChar), fontRenderContext);

            int width = (int) Math.ceil(stringBounds.getWidth());
            int height = (int) Math.ceil(stringBounds.getHeight());
            generatedChars++;

            maxX = Math.max(maxX, currentX + width);
            maxY = Math.max(maxY, currentY + height);

            if (charNX >= charsVert) {
                currentX = 0;
                currentY += currentRowMaxY + pixelPadding;
                charNX = 0;
                currentRowMaxY = 0;
            }

            currentRowMaxY = Math.max(currentRowMaxY, height);
            glyphs1.add(new Glyph(currentX, currentY, width, height, currentChar, this));

            currentX += width + pixelPadding;
            charNX++;
        }

        BufferedImage bufferedImage = new BufferedImage(
                Math.max(maxX + pixelPadding, 1),
                Math.max(maxY + pixelPadding, 1),
                BufferedImage.TYPE_INT_ARGB);

        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(new Color(255, 255, 255, 0));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE);

        g2d.setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_OFF);
        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);

        for (Glyph glyph : glyphs1) {
            g2d.setFont(getFontForGlyph(glyph.value()));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            g2d.drawString(String.valueOf(glyph.value()), glyph.u(), glyph.v() + fontMetrics.getAscent());
            glyphs.put(glyph.value(), glyph);
        }

        registerBufferedImageTexture(bindToTexture, bufferedImage);
        generated = true;
    }
}