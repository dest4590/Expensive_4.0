package ru.expensive.asm.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.expensive.api.event.EventManager;
import ru.expensive.common.util.player.MovingUtil;
import ru.expensive.implement.events.player.MovementInputEvent;
import ru.expensive.implement.events.player.RotatedMovementInputEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationPlan;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends InputMixin {

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/KeyboardInput;sneaking:Z", shift = At.Shift.AFTER), allow = 1)
    private void injectMovementInputEvent(boolean slowDown, float f, CallbackInfo ci) {
        var event = new MovementInputEvent(new MovingUtil.DirectionalInput(this.pressingForward, this.pressingBack,
                this.pressingLeft, this.pressingRight), this.jumping, this.sneaking);

        EventManager.callEvent(event);

        var directionalInput = event.getDirectionalInput();

        if (directionalInput != null) {
            this.pressingForward = directionalInput.isForwards();
            this.pressingBack = directionalInput.isBackwards();
            this.pressingLeft = directionalInput.isLeft();
            this.pressingRight = directionalInput.isRight();
            this.movementForward = KeyboardInput.getMovementMultiplier(directionalInput.isForwards(),
                    directionalInput.isBackwards());
            this.movementSideways = KeyboardInput.getMovementMultiplier(directionalInput.isLeft(),
                    directionalInput.isRight());

            fixStrafeMovement();

            this.jumping = event.isJumping();
            this.sneaking = event.isSneaking();
        }
    }

    private void fixStrafeMovement() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        RotationController rotationController = RotationController.INSTANCE;
        Angle angle = rotationController.getCurrentAngle();
        RotationPlan configurable = rotationController.getCurrentRotationPlan();

        float z = this.movementForward;
        float x = this.movementSideways;

        final RotatedMovementInputEvent MoveInputEvent;

        if (configurable == null || angle == null || player == null
                || !(configurable.isMoveCorrection() && configurable.isFreeCorrection())) {
            MoveInputEvent = new RotatedMovementInputEvent(z, x);
            EventManager.callEvent(MoveInputEvent);
        } else {
            float deltaYaw = player.getYaw() - angle.getYaw();

            float newX = x * MathHelper.cos(deltaYaw * 0.017453292f) - z * MathHelper.sin(deltaYaw * 0.017453292f);
            float newZ = z * MathHelper.cos(deltaYaw * 0.017453292f) + x * MathHelper.sin(deltaYaw * 0.017453292f);

            MoveInputEvent = new RotatedMovementInputEvent(Math.round(newZ), Math.round(newX));
            EventManager.callEvent(MoveInputEvent);
        }

        this.movementSideways = MoveInputEvent.getSideways();
        this.movementForward = MoveInputEvent.getForward();
    }
}
