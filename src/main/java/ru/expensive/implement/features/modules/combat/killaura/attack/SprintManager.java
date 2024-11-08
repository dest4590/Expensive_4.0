package ru.expensive.implement.features.modules.combat.killaura.attack;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import ru.expensive.api.feature.module.Module;
import ru.expensive.common.QuickImports;
import ru.expensive.core.Expensive;
import ru.expensive.core.listener.impl.PacketEventListener;
import ru.expensive.implement.features.modules.movement.AutoSprintModule;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class SprintManager implements QuickImports {
    @Setter
    Mode currentMode;
    boolean isStopSprintPacketSent;

    public void tick(AttackHandler parent) {
        if (currentMode == Mode.BYPASS) {
            Module autoSprintModule = Expensive.getInstance().getModuleProvider().module("AutoSprint");
            if (autoSprintModule instanceof AutoSprintModule autoSprint && autoSprintModule.isState()) {
                autoSprint.setEmergencyStop(true);
            }
        }
    }

    public void preAttack() {
        if (currentMode == Mode.DEFAULT) {
            if (PacketEventListener.serverSprint && mc.player.lastSprinting) {
                mc.player.setSprinting(false);
                mc.player.networkHandler
                        .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                isStopSprintPacketSent = true;
            }
        }
    }

    public void postAttack() {
        if (currentMode == Mode.DEFAULT) {
            if (isStopSprintPacketSent && (!PacketEventListener.serverSprint && mc.player.lastSprinting)) {
                mc.player.setSprinting(true);
                mc.player.networkHandler
                        .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                isStopSprintPacketSent = false;
            }
        }
    }

    public enum Mode {
        BYPASS, DEFAULT, NONE
    }
}
