package ru.expensive.implement.features.modules.combat;

import io.netty.buffer.Unpooled;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.jetbrains.annotations.NotNull;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.repository.friend.FriendRepository;
import ru.expensive.implement.events.packet.PacketEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoFriendDamageModule extends Module {
    public NoFriendDamageModule() {
        super("NoFriendDamage", "No Friend Damage", ModuleCategory.COMBAT);
    }

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        Packet<?> packet = packetEvent.getPacket();
        if (packetEvent.isSend() && packet instanceof PlayerInteractEntityC2SPacket interactEntityC2SPacket) {
            Entity entity = getEntity(interactEntityC2SPacket);
            InteractType interactType = getInteractType(interactEntityC2SPacket);

            if (validate(entity, interactType)) {
                packetEvent.cancel();
            }
        }
    }

    private boolean validate(Entity entity, InteractType interactType) {
        if (!FriendRepository.isFriend(entity.getEntityName())) {
            return false;
        }
        if (interactType != InteractType.ATTACK) {
            return false;
        }

        return entity instanceof PlayerEntity;
    }

    private Entity getEntity(@NotNull PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);

        return mc.world.getEntityById(packetBuf.readVarInt());
    }

    private InteractType getInteractType(@NotNull PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);

        packetBuf.readVarInt();
        return packetBuf.readEnumConstant(InteractType.class);
    }

    public enum InteractType {
        INTERACT, ATTACK, INTERACT_AT
    }
}
