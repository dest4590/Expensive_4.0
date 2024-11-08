package ru.expensive.asm.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.block.ShouldBlockVision;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockMixin {

    @Inject(method = "shouldBlockVision", at = @At("HEAD"), cancellable = true)
    public void shouldBlockVision(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> infoReturnable) {
        ShouldBlockVision event = new ShouldBlockVision();
        EventManager.callEvent(event);

        if (event.isCancelled()) {
            infoReturnable.setReturnValue(false);
        }
    }
}
