package ru.expensive.asm.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.system.draw.DrawEngineImpl;
import ru.expensive.common.util.math.MathUtil;
import ru.expensive.core.Expensive;
import ru.expensive.implement.events.render.DrawEvent;

@Mixin(InGameHud.class)
public class InGameMixin {

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        DrawEvent event = new DrawEvent(
                context,
                new DrawEngineImpl(),
                tickDelta);

        EventManager.callEvent(event);

        Expensive.getInstance()
                .getDraggableRepository()
                .draggable()
                .forEach(draggable -> {
                    draggable.tick(tickDelta);

                    if (draggable.visible()) {
                        draggable.startAnimation();
                    } else {
                        draggable.startCloseAnimation();
                    }

                    float scale = draggable
                            .getScaleAnimation()
                            .getOutput()
                            .floatValue();

                    if (!draggable.isCloseAnimationFinished()) {
                        MathUtil.scale(context.getMatrices(), draggable.getX() + (float) draggable.getWidth() / 2,
                                draggable.getY() + (float) draggable.getHeight() / 2, scale,
                                () -> draggable.drawDraggable(context));
                    }
                });
    }
}
