package ru.expensive.api.system.font.entry;

import ru.expensive.api.system.font.glyph.Glyph;

public record DrawEntry(float atX, float atY, int color, Glyph toDraw) {
}
