package ru.expensive.asm.mixins;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.keyboard.KeyEvent;
import ru.expensive.implement.screens.menu.MenuScreen;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (key != GLFW.GLFW_KEY_UNKNOWN && MinecraftClient.getInstance().currentScreen == null) {
            if (action == 0) {
                if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                    MinecraftClient.getInstance().setScreen(new MenuScreen());
                }
            }

            EventManager.callEvent(new KeyEvent(key, action));
        }
    }
}