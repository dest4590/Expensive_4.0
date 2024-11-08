package ru.expensive.implement.features.modules.combat.killaura.attack;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import ru.expensive.common.QuickImports;
import ru.expensive.implement.events.packet.PacketEvent;
import ru.expensive.implement.features.modules.combat.killaura.rotation.Angle;

import java.util.List;

@Getter
public class AttackPerpetrator implements QuickImports {
    AttackHandler attackHandler = new AttackHandler();

    public void tick() {
        attackHandler.tick();
    }

    public void onPacket(PacketEvent packet) {
        attackHandler.onPacket(packet);
    }

    public void performAttack(AttackPerpetratorConfigurable configurable) {
        attackHandler.sprintManager.setCurrentMode(configurable.getMode());
        attackHandler.handleAttack(configurable);
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class AttackPerpetratorConfigurable {
        LivingEntity target;
        Angle angle;
        float maximumRange;
        boolean raytraceEnabled;
        boolean onlyCritical;
        boolean shouldBreakShield;
        boolean shouldUnpressShield;
        boolean useDynamicCooldown;
        SprintManager.Mode mode;

        public AttackPerpetratorConfigurable(LivingEntity target, Angle angle, float maximumRange, List<String> options,
                                             SprintManager.Mode mode) {
            this.target = target;
            this.angle = angle;
            this.maximumRange = maximumRange;
            this.raytraceEnabled = options.contains("Raytrace check");
            this.onlyCritical = options.contains("Only Critical");
            this.shouldBreakShield = options.contains("Break Shield");
            this.shouldUnpressShield = options.contains("Un Press Shield");
            this.useDynamicCooldown = options.contains("Dynamic Cooldown");
            this.mode = mode;
        }
    }
}
