package ru.expensive.asm.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.event.types.EventType;
import ru.expensive.implement.events.player.AttackEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void attackEntityPre(PlayerEntity player, Entity target, CallbackInfo callbackInfo) {
        AttackEvent event = new AttackEvent(
                target,
                EventType.PRE);

        EventManager.callEvent(event);
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    public void attackEntityPost(PlayerEntity player, Entity target, CallbackInfo callbackInfo) {
        AttackEvent event = new AttackEvent(
                target,
                EventType.POST);

        EventManager.callEvent(event);
    }

    @ModifyArgs(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket$Full;<init>(DDDFFZ)V"))
    private void hookFixRotation(Args args) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();
        if (angle == null) {
            return;
        }

        args.set(3, angle.getYaw());
        args.set(4, angle.getPitch());
    }
}
