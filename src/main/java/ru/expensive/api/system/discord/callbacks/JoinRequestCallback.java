package ru.expensive.api.system.discord.callbacks;

import com.sun.jna.Callback;
import ru.expensive.api.system.discord.utils.DiscordUser;

public interface JoinRequestCallback extends Callback {
    void apply(DiscordUser var1);
}