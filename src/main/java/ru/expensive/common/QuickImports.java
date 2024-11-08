package ru.expensive.common;

import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.Window;
import ru.expensive.api.system.draw.DrawEngine;
import ru.expensive.api.system.draw.DrawEngineImpl;
import ru.expensive.api.system.shape.implement.Image;
import ru.expensive.api.system.shape.implement.Rectangle;
import ru.expensive.implement.screens.menu.components.implement.window.WindowManager;

public interface QuickImports extends QuickLogger {
    MinecraftClient mc = MinecraftClient.getInstance();
    Window window = mc.getWindow();

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();

    DrawEngine drawEngine = new DrawEngineImpl();

    Rectangle rectangle = new Rectangle();
    Image image = new Image();

    Gson gson = new Gson();

    WindowManager windowManager = new WindowManager();
}
