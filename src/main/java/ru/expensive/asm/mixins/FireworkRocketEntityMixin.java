package ru.expensive.asm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d tick(LivingEntity instance) {

        if (instance != MinecraftClient.getInstance().player) {
            return instance.getRotationVector();
        }

        var rotationManager = RotationController.INSTANCE;
        var rotation = rotationManager.getRotation();
        var configurable = rotationManager.getCurrentRotationPlan();

        if (rotation == null || configurable == null || !configurable.isMoveCorrection()
                || configurable.isChangeLook()) {
            return instance.getRotationVector();
        }

        return rotation.toVector();
    }
}
