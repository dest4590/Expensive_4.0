package ru.expensive.asm.mixins;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderStage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.system.shader.Shader;
import ru.expensive.implement.events.render.AspectRatioEvent;
import ru.expensive.implement.events.render.WorldRenderEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RaytracingUtil;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RotationController;

import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    MinecraftClient client;
    @Shadow
    private float zoom;

    @Shadow
    private float zoomX;
    @Shadow
    @Final
    private Camera camera;
    @Shadow
    private float zoomY;
    @Shadow
    private float viewDistance;

    @Inject(method = "loadPrograms", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    void loadAllTheShaders(ResourceFactory factory, CallbackInfo ci, List<ShaderStage> stages,
                           List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shadersToLoad) {
        Shader.REGISTERED_PROGRAMS.forEach(loader -> shadersToLoad.add(
                new Pair<>(
                        loader.getLeft().apply(factory),
                        loader.getRight())));
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;raycast(DFZ)Lnet/minecraft/util/hit/HitResult;"))
    private HitResult hookRaycast(Entity instance, double maxDistance, float tickDelta, boolean includeFluids) {
        if (instance != client.player)
            return instance.raycast(maxDistance, tickDelta, includeFluids);

        Angle currentAngle = RotationController.INSTANCE.getCurrentAngle();
        Angle angle = currentAngle != null ? currentAngle
                : new Angle(instance.getYaw(tickDelta), instance.getPitch(tickDelta));

        return RaytracingUtil.raycast(maxDistance, angle, includeFluids);
    }

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d hookRotationVector(Entity instance, float tickDelta) {
        Angle angle = RotationController.INSTANCE.getCurrentAngle();

        return angle != null ? angle.toVector() : instance.getRotationVec(tickDelta);
    }

    @Inject(method = "getBasicProjectionMatrix", at = @At("TAIL"), cancellable = true)
    public void getBasicProjectionMatrixHook(double fov, CallbackInfoReturnable<Matrix4f> cir) {
        AspectRatioEvent aspectRatioEvent = new AspectRatioEvent();
        EventManager.callEvent(aspectRatioEvent);
        if (aspectRatioEvent.isCancelled()) {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.peek().getPositionMatrix().identity();
            if (zoom != 1.0f) {
                matrixStack.translate(zoomX, -zoomY, 0.0f);
                matrixStack.scale(zoom, zoom, 1.0f);
            }
            matrixStack.peek().getPositionMatrix().mul(new Matrix4f().setPerspective(
                    (float) (fov * 0.01745329238474369), aspectRatioEvent.getRatio(), 0.05f, viewDistance * 4.0f));
            cir.setReturnValue(matrixStack.peek().getPositionMatrix());
        }
    }

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=hand", shift = At.Shift.BEFORE))
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        EventManager.callEvent(new WorldRenderEvent(matrices, tickDelta));
    }
}
