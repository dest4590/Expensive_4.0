package ru.expensive.asm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;

@Mixin(Item.class)
public class ItemMixin {

    @Redirect(method = "raycast", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private static float hookFixRotationA(PlayerEntity instance) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();
        if (instance != MinecraftClient.getInstance().player || angle == null) {
            return instance.getYaw();
        }

        return angle.getYaw();
    }

    @Redirect(method = "raycast", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getPitch()F"))
    private static float hookFixRotationB(PlayerEntity instance) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();
        if (instance != MinecraftClient.getInstance().player || angle == null) {
            return instance.getPitch();
        }

        return angle.getPitch();
    }
}
