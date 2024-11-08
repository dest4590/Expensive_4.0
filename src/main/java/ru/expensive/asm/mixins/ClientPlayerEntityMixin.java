package ru.expensive.asm.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.event.events.Event;
import ru.expensive.implement.events.block.PushBlockEvent;
import ru.expensive.implement.events.player.MotionEvent;
import ru.expensive.implement.events.player.PostMotionEvent;
import ru.expensive.implement.events.player.PostTickEvent;
import ru.expensive.implement.events.player.TickEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    @Unique
    private final MinecraftClient minecraft = MinecraftClient.getInstance();

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE))
    public void tick(CallbackInfo callbackInfo) {
        if (minecraft.player != null && minecraft.world != null) {
            Event event = EventManager.callEvent(new TickEvent());
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER))
    public void postTick(CallbackInfo callbackInfo) {
        if (minecraft.player != null && minecraft.world != null) {
            EventManager.callEvent(new PostTickEvent());
        }
    }

    @ModifyExpressionValue(method = {"sendMovementPackets",
            "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float hookSilentRotationYaw(float original) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();
        if (angle == null) {
            return original;
        }

        return angle.getYaw();
    }

    @ModifyExpressionValue(method = {"sendMovementPackets",
            "tick"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float hookSilentRotationPitch(float original) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();
        if (angle == null) {
            return original;
        }

        return angle.getPitch();
    }

    @Inject(method = {"sendMovementPackets"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void preMotion(CallbackInfo info) {
        MotionEvent eventMotion = new MotionEvent(getX(), getY(), getZ(), getYaw(1), getPitch(1), isOnGround());
        EventManager.callEvent(eventMotion);

        if (eventMotion.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"), cancellable = true)
    private void postMotion(CallbackInfo info) {
        PostMotionEvent postMotionEvent = new PostMotionEvent();
        EventManager.callEvent(postMotionEvent);

        if (postMotionEvent.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double z, CallbackInfo callbackInfo) {
        PushBlockEvent pushBlockEvent = new PushBlockEvent();
        EventManager.callEvent(pushBlockEvent);

        if (pushBlockEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
