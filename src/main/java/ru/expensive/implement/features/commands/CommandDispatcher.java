/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.expensive.implement.features.commands;

import net.minecraft.util.Pair;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.feature.command.argument.ICommandArgument;
import ru.expensive.api.feature.command.exception.CommandNotEnoughArgumentsException;
import ru.expensive.api.feature.command.exception.CommandNotFoundException;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.api.feature.command.manager.ICommandManager;
import ru.expensive.common.QuickLogger;
import ru.expensive.core.Expensive;
import ru.expensive.implement.events.chat.ChatEvent;
import ru.expensive.implement.events.chat.TabCompleteEvent;
import ru.expensive.implement.features.commands.argument.ArgConsumer;
import ru.expensive.implement.features.commands.argument.CommandArguments;
import ru.expensive.implement.features.commands.manager.CommandRepository;

import java.util.List;
import java.util.stream.Stream;

import static ru.expensive.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

public class CommandDispatcher implements QuickLogger {

    private final ICommandManager manager;

    public CommandDispatcher(EventManager eventManager) {
        this.manager = Expensive.getInstance().getCommandRepository();
        eventManager.register(this);
    }

    @EventHandler
    public void onSendChatMessage(ChatEvent event) {
        String msg = event.getMessage();
        String prefix = ".";
        boolean forceRun = msg.startsWith(FORCE_COMMAND_PREFIX);
        if ((msg.startsWith(prefix)) || forceRun) {
            event.cancel();
            String commandStr = msg.substring(forceRun ? FORCE_COMMAND_PREFIX.length() : prefix.length());
            if (!runCommand(commandStr) && !commandStr.trim().isEmpty()) {
                new CommandNotFoundException(CommandRepository.expand(commandStr).getLeft()).handle(null, null);
            }
        } else if (runCommand(msg)) {
            event.cancel();
        }
    }

    public boolean runCommand(String msg) {
        if (msg.isEmpty()) {
            return this.runCommand("help");
        }
        Pair<String, List<ICommandArgument>> pair = CommandRepository.expand(msg);
        String command = pair.getLeft();
        String rest = msg.substring(pair.getLeft().length());
        ArgConsumer argc = new ArgConsumer(this.manager, pair.getRight());
        /*
         * if (!argc.hasAny()) {
         * Settings.Setting setting =
         * settings.byLowerName.get(command.toLowerCase(Locale.US));
         * if (setting != null) {
         * logRanCommand(command, rest);
         * if (setting.getValueClass() == Boolean.class) {
         * this.manager.execute(String.format("set toggle %s", setting.getName()));
         * } else {
         * this.manager.execute(String.format("set %s", setting.getName()));
         * }
         * return true;
         * }
         * } else if (argc.hasExactlyOne()) {
         * for (Settings.Setting setting : settings.allSettings) {
         * if (setting.isJavaOnly()) {
         * continue;
         * }
         * if (setting.getName().equalsIgnoreCase(pair.getA())) {
         * logRanCommand(command, rest);
         * try {
         * this.manager.execute(String.format("set %s %s", setting.getName(),
         * argc.getString()));
         * } catch (CommandNotEnoughArgumentsException ignored) {
         * } // The operation is safe
         * return true;
         * }
         * }
         * }
         */

        // If the command exists, then handle echoing the input

        return this.manager.execute(pair);
    }

    @EventHandler
    public void onPreTabComplete(TabCompleteEvent event) {

        String prefix = event.prefix;
        String commandPrefix = ".";
        if (!prefix.startsWith(commandPrefix)) {
            return;
        }
        String msg = prefix.substring(commandPrefix.length());
        List<ICommandArgument> args = CommandArguments.from(msg, true);
        Stream<String> stream = tabComplete(msg);
        if (args.size() == 1) {
            stream = stream.map(x -> commandPrefix + x);
        }
        event.completions = stream.toArray(String[]::new);
    }

    public Stream<String> tabComplete(String msg) {
        try {
            List<ICommandArgument> args = CommandArguments.from(msg, true);
            ArgConsumer argc = new ArgConsumer(this.manager, args);
            if (argc.hasAtMost(2)) {
                if (argc.hasExactly(1)) {
                    return new TabCompleteHelper()
                            .addCommands(this.manager)
                            .filterPrefix(argc.getString())
                            .stream();
                }
                /*
                 * Settings.Setting setting =
                 * settings.byLowerName.get(argc.getString().toLowerCase(Locale.US));
                 * if (setting != null && !setting.isJavaOnly()) {
                 * if (setting.getValueClass() == Boolean.class) {
                 * TabCompleteHelper helper = new TabCompleteHelper();
                 * if ((Boolean) setting.value) {
                 * helper.append("true", "false");
                 * } else {
                 * helper.append("false", "true");
                 * }
                 * return helper.filterPrefix(argc.getString()).stream();
                 * } else {
                 * return Stream.of(SettingsUtil.settingValueToString(setting));
                 * }
                 * }
                 */
            }
            return this.manager.tabComplete(msg);
        } catch (CommandNotEnoughArgumentsException ignored) { // Shouldn't happen, the operation is safe
            return Stream.empty();
        }
    }
}
