package ru.expensive.core.listener.impl;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import ru.expensive.api.event.EventHandler;
import ru.expensive.core.Expensive;
import ru.expensive.core.listener.Listener;
import ru.expensive.implement.events.packet.PacketEvent;

public class PacketEventListener implements Listener {
    public static boolean serverSprint;

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        Packet<?> packet = packetEvent.getPacket();
        if (packet instanceof ClientCommandC2SPacket clientCommandC2SPacket) {
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                serverSprint = true;
            }
            if (clientCommandC2SPacket.getMode() == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                serverSprint = false;
            }
        }
        Expensive.getInstance().getAttackPerpetrator().onPacket(packetEvent);
    }
}
