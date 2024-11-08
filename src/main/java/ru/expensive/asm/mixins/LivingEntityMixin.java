package ru.expensive.asm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.block.PushPlayerEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;

@SuppressWarnings("all")
@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void isPushable(CallbackInfoReturnable<Boolean> infoReturnable) {
        PushPlayerEvent pushPlayerEvent = new PushPlayerEvent();
        EventManager.callEvent(pushPlayerEvent);

        if ((Object) this instanceof ClientPlayerEntity && pushPlayerEvent.isCancelled()) {
            infoReturnable.setReturnValue(false);
        }
    }

    @Redirect(method = "jump", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookFixRotation(Vec3d instance, double x, double y, double z) {
        var rotationManager = RotationController.INSTANCE;
        var rotation = rotationManager.getRotation();
        var configurable = rotationManager.getCurrentRotationPlan();

        if ((Object) this != MinecraftClient.getInstance().player) {
            return instance.add(x, y, z);
        }

        if (configurable == null || !configurable.isMoveCorrection() || rotation == null) {
            return instance.add(x, y, z);
        }

        float yaw = rotation.getYaw() * 0.017453292F;

        return instance.add(-MathHelper.sin(yaw) * 0.2F, 0.0, MathHelper.cos(yaw) * 0.2F);
    }

    /**
     * Fall flying using modified-rotation
     */
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getPitch()F"))
    private float hookModifyFallFlyingPitch(LivingEntity instance) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return instance.getPitch();
        }

        var rotationManager = RotationController.INSTANCE;
        var rotation = rotationManager.getRotation();
        var configurable = rotationManager.getCurrentRotationPlan();

        if (rotation == null || configurable == null || !configurable.isMoveCorrection()
                || configurable.isChangeLook()) {
            return instance.getPitch();
        }

        return rotation.getPitch();
    }

    /**
     * Fall flying using modified-rotation
     */
    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookModifyFallFlyingRotationVector(LivingEntity original) {
        if ((Object) this != MinecraftClient.getInstance().player) {
            return original.getRotationVector();
        }

        var rotationManager = RotationController.INSTANCE;
        var rotation = rotationManager.getRotation();
        var configurable = rotationManager.getCurrentRotationPlan();

        if (rotation == null || configurable == null || !configurable.isMoveCorrection()
                || configurable.isChangeLook()) {
            return original.getRotationVector();
        }

        return rotation.toVector();
    }
}
