package ru.expensive.implement.features.modules.combat.killaura.rotation;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.feature.module.Module;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.task.TaskPriority;
import ru.expensive.common.util.task.TaskProcessor;
import ru.expensive.core.Expensive;
import ru.expensive.implement.events.packet.PacketEvent;
import ru.expensive.implement.events.player.MovementInputEvent;
import ru.expensive.implement.events.player.PlayerVelocityStrafeEvent;
import ru.expensive.implement.events.player.PostRotationMovementInputEvent;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RotationController implements QuickImports {

    public static RotationController INSTANCE = new RotationController();

    public RotationController() {
        Expensive.getInstance().getEventManager().register(this);
    }

    RotationPlan lastRotationPlan;

    final TaskProcessor<RotationPlan> rotationPlanTaskProcessor = new TaskProcessor<>();

    Angle currentAngle;

    Angle previousAngle;

    Angle serverAngle = Angle.DEFAULT;


    public void setRotation(Angle value) {
        if (value == null) {
            this.previousAngle = this.currentAngle != null ? this.currentAngle : (mc.player != null ? new Angle(mc.player.getYaw(), mc.player.getPitch()) : Angle.DEFAULT);
        } else {
            this.previousAngle = this.currentAngle;
        }
        this.currentAngle = value;
    }

    public Angle getRotation() {
        Vec2f playerRotation = mc.player.getRotationClient();
        return currentAngle != null ? currentAngle : AngleUtil.fromVec2f(playerRotation);
    }

    public RotationPlan getCurrentRotationPlan() {
        return rotationPlanTaskProcessor.fetchActiveTaskValue() != null ? rotationPlanTaskProcessor.fetchActiveTaskValue() : lastRotationPlan;
    }

    public void rotateTo(Angle.VecRotation vecRotation, LivingEntity entity, RotationConfig configurable, TaskPriority taskPriority, Module provider) {
        rotateTo(configurable.createRotationPlan(vecRotation.getAngle(), vecRotation.getVec(), entity), taskPriority, provider);
    }

    public void rotateTo(Angle angle, RotationConfig configurable, TaskPriority taskPriority, Module provider) {
        rotateTo(configurable.createRotationPlan(angle), taskPriority, provider);
    }

    public void rotateTo(RotationPlan plan, TaskPriority taskPriority, Module provider) {
        rotationPlanTaskProcessor.addTask(new TaskProcessor.Task<>(plan.isChangeLook() ? 1 : plan.getTicksUntilReset(), taskPriority.getPriority(), provider, plan));
    }

    public void update() {
        ClientPlayerEntity player = mc.player;
        if (player == null) {
            return;
        }

        RotationPlan activePlan = getCurrentRotationPlan();
        if (activePlan == null) {
            return;
        }
        Vec2f playerRotation = player.getRotationClient();
        if (rotationPlanTaskProcessor.fetchActiveTaskValue() == null) {
            double differenceFromCurrentToPlayer = computeRotationDifference(serverAngle, AngleUtil.fromVec2f(playerRotation));
            if (differenceFromCurrentToPlayer < activePlan.getResetThreshold() || activePlan.isChangeLook()) {
                if (currentAngle != null) {
                    player.setYaw(currentAngle.getYaw() + computeAngleDifference(player.getYaw(), currentAngle.getYaw()));
                    player.renderYaw = player.getYaw();
                    player.lastRenderYaw = player.getYaw();
                }
                setRotation(null);
                lastRotationPlan = null;
                return;
            }
        }
        Angle newAngle = activePlan.nextRotation(currentAngle != null ? currentAngle :
                        AngleUtil.fromVec2f(playerRotation), rotationPlanTaskProcessor.fetchActiveTaskValue() == null)
                .adjustSensitivity();
        setRotation(newAngle);
        lastRotationPlan = activePlan;
        if (activePlan.isChangeLook()) {
            applyPlayerRotation(mc.player, newAngle);
        }
        rotationPlanTaskProcessor.tick(1);
    }

    public static void applyPlayerRotation(PlayerEntity player, Angle angle) {
        if (player == null) {
            return;
        }

        player.prevPitch = player.getPitch();
        player.prevYaw = player.getYaw();

        Angle newAngle = angle.adjustSensitivity();
        player.setYaw(newAngle.getYaw());
        player.setPitch(newAngle.getPitch());
    }

    public static double computeRotationDifference(Angle a, Angle b) {
        return Math.hypot(Math.abs(computeAngleDifference(a.getYaw(), b.getYaw())), Math.abs(a.getPitch() - b.getPitch()));
    }

    public static float computeAngleDifference(float a, float b) {
        return MathHelper.wrapDegrees(a - b);
    }

    @EventHandler
    public void velocityHandler(PlayerVelocityStrafeEvent event) {
        RotationPlan currentRotationPlan = getCurrentRotationPlan();
        if (currentRotationPlan == null) {
            return;
        }
        if (currentRotationPlan.isMoveCorrection()) {
            event.setVelocity(fixVelocity(event.getVelocity(), event.getMovementInput(), event.getSpeed()));
        }
    }

    private Vec3d fixVelocity(Vec3d currVelocity, Vec3d movementInput, float speed) {
        if (currentAngle != null) {
            Angle rotation = currentAngle;
            float yaw = rotation.getYaw();
            double d = movementInput.lengthSquared();

            if (d < 1.0E-7) {
                return Vec3d.ZERO;
            } else {
                Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);

                float f = MathHelper.sin(yaw * 0.017453292f);
                float g = MathHelper.cos(yaw * 0.017453292f);

                return new Vec3d(
                        vec3d.getX() * g - vec3d.getZ() * f,
                        vec3d.getY(),
                        vec3d.getZ() * g + vec3d.getX() * f
                );
            }
        }

        return currVelocity;
    }

    @EventHandler
    public void onMovementInput(MovementInputEvent movementInputEvent) {
        EventManager.callEvent(new PostRotationMovementInputEvent());
        update();
    }

    @EventHandler
    public void handlePacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        Angle angle;
        if (packet instanceof PlayerMoveC2SPacket && ((PlayerMoveC2SPacket) packet).changesLook()) {
            angle = new Angle(((PlayerMoveC2SPacket) packet).getYaw(1), ((PlayerMoveC2SPacket) packet).getPitch(1));
        } else if (packet instanceof PlayerPositionLookS2CPacket) {
            angle = new Angle(((PlayerPositionLookS2CPacket) packet).getYaw(), ((PlayerPositionLookS2CPacket) packet).getPitch());
        } else {
            return;
        }

        if (!event.isCancelled()) {
            serverAngle = angle;
        }
    }
}

