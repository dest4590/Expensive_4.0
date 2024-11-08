package ru.expensive.asm.mixins;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationPlan;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER))
    private void injectQuickPerspectiveSwap(BlockView area, Entity focusedEntity, boolean thirdPerson,
                                            boolean inverseView, float tickDelta, CallbackInfo ci) {
        RotationController rotationController = RotationController.INSTANCE;
        RotationPlan rotationPlan = rotationController.getCurrentRotationPlan();
        Angle previousAngle = rotationController.getPreviousAngle();
        Angle currentAngle = rotationController.getCurrentAngle();

        boolean shouldModifyRotation = rotationPlan != null && rotationPlan.isChangeLook();

        if (currentAngle == null || previousAngle == null || !shouldModifyRotation) {
            return;
        }

        this.setRotation(
                MathHelper.lerp(tickDelta, previousAngle.getYaw(), currentAngle.getYaw()),
                MathHelper.lerp(tickDelta, previousAngle.getPitch(), currentAngle.getPitch()));
    }
}