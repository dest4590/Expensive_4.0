package ru.expensive.implement.features.modules.combat;

import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.implement.events.packet.PacketEvent;
import ru.expensive.implement.events.player.TickEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AntiBot extends Module {
    static Set<UUID> botSet = new HashSet<>();
    Set<UUID> suspectSet = new HashSet<>();

    public AntiBot() {
        super("AntiBot", ModuleCategory.COMBAT);
    }

    public static boolean isBot(PlayerEntity entity) {
        return botSet.contains(entity.getUuid());
    }

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        Packet<?> packet = packetEvent.getPacket();

        if (packet instanceof PlayerListS2CPacket listPacket) {
            checkPlayerAfterSpawn(listPacket);
        } else if (packet instanceof PlayerRemoveS2CPacket removePacket) {
            removePlayerBecauseLeftServer(removePacket);
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!suspectSet.isEmpty()) {
            mc.world.getPlayers().stream()
                    .filter(player -> suspectSet.contains(player.getUuid()))
                    .forEach(this::evaluateSuspectPlayer);
        }
    }

    private void checkPlayerAfterSpawn(PlayerListS2CPacket listS2CPacket) {
        listS2CPacket.getPlayerAdditionEntries().forEach(entry -> {
            GameProfile profile = entry.profile();
            if (profile == null || isRealPlayer(entry, profile)) {
                return;
            }

            if (isDuplicateProfile(profile)) {
                botSet.add(profile.getId());
            } else {
                suspectSet.add(profile.getId());
            }
        });
    }

    private void removePlayerBecauseLeftServer(PlayerRemoveS2CPacket removeS2CPacket) {
        removeS2CPacket.profileIds().forEach(uuid -> {
            suspectSet.remove(uuid);
            botSet.remove(uuid);
        });
    }

    private boolean isRealPlayer(PlayerListS2CPacket.Entry entry, GameProfile profile) {
        return entry.latency() < 2 || (profile.getProperties() != null && !profile.getProperties().isEmpty());
    }

    private void evaluateSuspectPlayer(PlayerEntity player) {
        Iterable<ItemStack> armor = null;

        if (!isFullyEquipped(player)) {
            armor = player.getArmorItems();
        }
        if ((isFullyEquipped(player) || hasArmorChanged(player, armor))) {
            botSet.add(player.getUuid());
        }
        suspectSet.remove(player.getUuid());
    }

    public boolean isDuplicateProfile(GameProfile profile) {
        return mc.getNetworkHandler().getPlayerList().stream()
                .filter(player -> player.getProfile().getName().equals(profile.getName())
                        && !player.getProfile().getId().equals(profile.getId()))
                .count() == 1;
    }

    public boolean isFullyEquipped(PlayerEntity entity) {
        return IntStream.rangeClosed(0, 3)
                .mapToObj(entity.getInventory()::getArmorStack)
                .allMatch(stack -> stack.getItem() instanceof ArmorItem && !stack.hasEnchantments());
    }

    public boolean hasArmorChanged(PlayerEntity entity, Iterable<ItemStack> prevArmor) {
        if (prevArmor == null) {
            return true;
        }

        List<ItemStack> currentArmorList = StreamSupport.stream(entity.getArmorItems().spliterator(), false).toList();
        List<ItemStack> prevArmorList = StreamSupport.stream(prevArmor.spliterator(), false).toList();

        return !IntStream.range(0, Math.min(currentArmorList.size(), prevArmorList.size()))
                .allMatch(i -> currentArmorList.get(i).equals(prevArmorList.get(i)))
                || currentArmorList.size() != prevArmorList.size();
    }

    public void reset() {
        suspectSet.clear();
        botSet.clear();
    }

    @Override
    public void deactivate() {
        reset();
        super.deactivate();
    }
}