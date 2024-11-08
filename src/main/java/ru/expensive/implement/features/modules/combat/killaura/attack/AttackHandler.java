package ru.expensive.implement.features.modules.combat.killaura.attack;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import ru.expensive.common.QuickImports;
import ru.expensive.common.util.player.PlayerIntersectionUtil;
import ru.expensive.implement.events.packet.PacketEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.RaytracingUtil;

@Getter
public class AttackHandler implements QuickImports {
    SprintManager sprintManager = new SprintManager();
    ClickScheduler clickScheduler = new ClickScheduler();

    void tick() {
        sprintManager.tick(this);
    }

    public void onPacket(PacketEvent packetEvent) {
        Packet<?> packet = packetEvent.getPacket();
        if (packet instanceof UpdateSelectedSlotC2SPacket) {
            clickScheduler.recalculate(650L);
        } else if (packet instanceof HandSwingC2SPacket) {
            clickScheduler.recalculate(500L);
        }
    }

    void handleAttack(AttackPerpetrator.AttackPerpetratorConfigurable configurable) {
        if (canAttack(configurable)) {
            if (mc.player.isBlocking() && configurable.isShouldUnpressShield()) {
                mc.interactionManager.stopUsingItem(mc.player);
            }
            sprintManager.preAttack();
            mc.player.networkHandler
                    .sendPacket(PlayerInteractEntityC2SPacket.attack(configurable.getTarget(), mc.player.isSneaking()));
            mc.player.resetLastAttackedTicks();
            mc.player.swingHand(Hand.MAIN_HAND);
            sprintManager.postAttack();
        }
    }

    boolean canAttack(AttackPerpetrator.AttackPerpetratorConfigurable config) {
        Entity targetEntity = getRayTracingEntity(config);

        if (isRaytraceCheckFailed(config, targetEntity) || isCooldownNotComplete(config)) {
            return false;
        }

        if (config.isOnlyCritical() && !hasMovementRestrictions()) {
            return isPlayerInCriticalState();
        }

        return true;
    }

    private boolean isRaytraceCheckFailed(AttackPerpetrator.AttackPerpetratorConfigurable config, Entity targetEntity) {
        return config.isRaytraceEnabled() && targetEntity != config.getTarget();
    }

    private boolean isCooldownNotComplete(AttackPerpetrator.AttackPerpetratorConfigurable config) {
        return !clickScheduler.isCooldownComplete(config.isUseDynamicCooldown());
    }

    private boolean hasMovementRestrictions() {
        return mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
                || PlayerIntersectionUtil.isPlayerInWeb()
                || mc.player.isSubmergedInWater()
                || mc.player.isInLava()
                || mc.player.isClimbing()
                || mc.player.getAbilities().flying;
    }

    private boolean isPlayerInCriticalState() {
        return !mc.player.isOnGround() && mc.player.fallDistance > 0;
    }

    private Entity getRayTracingEntity(AttackPerpetrator.AttackPerpetratorConfigurable configurable) {
        EntityHitResult entityHitResult = RaytracingUtil.raytraceEntity(
                configurable.getMaximumRange(),
                configurable.getAngle(),
                e -> configurable.isRaytraceEnabled());

        if (entityHitResult != null) {
            return entityHitResult.getEntity();
        }
        return null;
    }
}
