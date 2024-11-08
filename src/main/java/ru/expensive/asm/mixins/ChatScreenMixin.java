package ru.expensive.asm.mixins;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.api.feature.draggable.AbstractDraggable;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Expensive;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin extends Screen implements QuickImports {

    @Unique
    List<AbstractDraggable> draggables = Expensive.getInstance()
            .getDraggableRepository()
            .draggable();

    protected ChatScreenMixin() {
        super(Text.empty());
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        draggables.stream()
                .filter(draggable -> draggable.visible() && draggable.isDragging())
                .reduce((first, second) -> second)
                .ifPresent(active -> draggables.forEach(draggable -> {
                    if (active == draggable) {
                        draggable.render(context, mouseX, mouseY, delta);
                    }
                }));
    }

    @Inject(method = "mouseClicked", at = @At("TAIL"))
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        draggables.forEach(draggable -> draggable.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggables.forEach(draggable -> draggable.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
