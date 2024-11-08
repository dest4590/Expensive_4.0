package ru.expensive.asm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.expensive.api.event.EventManager;
import ru.expensive.implement.events.player.BoundingBoxControlEvent;
import ru.expensive.implement.events.player.PlayerVelocityStrafeEvent;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private Box boundingBox;

    @Shadow
    public static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookVelocity(Vec3d movementInput, float speed, float yaw) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            PlayerVelocityStrafeEvent event = new PlayerVelocityStrafeEvent(movementInput, speed, yaw,
                    EntityMixin.movementInputToVelocity(movementInput, speed, yaw));
            EventManager.callEvent(event);
            return event.getVelocity();
        }

        return EntityMixin.movementInputToVelocity(movementInput, speed, yaw);
    }

    @Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
    public final void getBoundingBox(CallbackInfoReturnable<Box> cir) {
        Entity entity = (Entity) (Object) this;

        BoundingBoxControlEvent event = new BoundingBoxControlEvent(boundingBox, entity);
        EventManager.callEvent(event);
        if (event.isCancelled()) {
            cir.setReturnValue(event.getChangedBox());
        }
    }
}
