package ru.expensive.api.system.font;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import ru.expensive.core.Expensive;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Fonts {

    private static final Map<FontKey, FontRenderer> fontCache = new HashMap<>();

    @SneakyThrows
    public static FontRenderer create(float size, String name) {
        String path = "assets/minecraft/expensive/fonts/" + name + ".otf";

        try (InputStream inputStream = Expensive.class.getClassLoader().getResourceAsStream(path)) {
            Font font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(inputStream))
                    .deriveFont(Font.PLAIN, size / 2f);

            return new FontRenderer(font, size / 2f);
        }
    }

    public static void init() {
        for (Type type : Type.values()) {
            for (int size = 4; size <= 32; size++) {
                fontCache.put(new FontKey(size, type), create(size, type.getType()));
            }
        }
    }

    public static FontRenderer getSize(int size) {
        return getSize(size, Type.BOLD);
    }

    public static FontRenderer getSize(int size, Type type) {
        return fontCache.computeIfAbsent(new FontKey(size, type), k -> create(size, type.getType()));
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        DEFAULT("sfpromedium"),
        BOLD("sfprosemibold");

        private final String type;
    }

    private record FontKey(int size, Type type) {
    }
}