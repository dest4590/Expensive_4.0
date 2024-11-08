package ru.expensive.api.repository.friend;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FriendRepository {
    @Getter
    private static final List<Friend> friends = new ArrayList<>();

    public static void addFriend(String name) {
        friends.add(new Friend(name));
    }

    public static void removeFriend(String name) {
        friends.removeIf(friend -> friend.getName().equalsIgnoreCase(name));
    }

    public static boolean shouldAttack(PlayerEntity player) {
        return !isFriend(player.getEntityName());
    }

    public static boolean isFriend(String friend) {
        return friends.stream()
                .anyMatch(isFriend -> isFriend.getName().equals(friend));
    }

    public static void clear() {
        friends.clear();
    }

}
