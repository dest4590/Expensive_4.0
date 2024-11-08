package ru.expensive.asm.mixins;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.block.PushWaterEvent;
import ru.expensive.implement.events.player.KeepSprintEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationPlan;

@SuppressWarnings("all")
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "isPushedByFluids", at = @At("HEAD"), cancellable = true)
    public void isPushedByFluids(CallbackInfoReturnable<Boolean> infoReturnable) {
        PushWaterEvent pushWaterEvent = new PushWaterEvent();
        EventManager.callEvent(pushWaterEvent);

        if ((Object) this instanceof ClientPlayerEntity && pushWaterEvent.isCancelled()) {
            infoReturnable.setReturnValue(false);
        }
    }

    @Inject(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void attackHook(CallbackInfo callbackInfo) {
        EventManager.callEvent(new KeepSprintEvent());
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private float hookFixRotation(PlayerEntity entity) {
        RotationController rotationController = RotationController.INSTANCE;
        Angle angle = rotationController.getCurrentAngle();
        RotationPlan configurable = rotationController.getCurrentRotationPlan();

        if (configurable == null || angle == null) {
            return entity.getYaw();
        }

        return angle.getYaw();
    }

}
