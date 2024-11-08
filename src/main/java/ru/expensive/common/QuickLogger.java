package ru.expensive.common;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.stream.Stream;

public interface QuickLogger {
    static Text getPrefix() {
        MutableText expensive = Text.literal("Delta Client");
        expensive.setStyle(expensive.getStyle().withColor(Formatting.WHITE));

        MutableText prefix = Text.literal("");
        prefix.setStyle(expensive.getStyle().withColor(Formatting.DARK_GRAY));
        prefix.append(expensive);
        prefix.append(" -> ");

        return prefix;
    }

    default void logDirect(Text... components) {
        MutableText component = Text.literal("");
        component.append(getPrefix());
        component.append(Text.literal(" "));
        Arrays.asList(components).forEach(component::append);
        if (MinecraftClient.getInstance().player != null) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(component);
        }
    }

    default void logDirect(String message, Formatting color) {
        Stream.of(message.split("\n")).forEach(line -> {
            MutableText component = Text.literal(line.replace("\t", "    "));
            component.setStyle(component.getStyle().withColor(color));
            logDirect(component);
        });
    }

    default void logDirect(String message) {
        logDirect(message, Formatting.GRAY);
    }
}
