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

package ru.expensive.implement.features.commands.defaults;

import ru.expensive.api.feature.command.ICommand;
import ru.expensive.core.Expensive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class DefaultCommands {

    public static List<ICommand> createAll() {
        Expensive expensive = Expensive.getInstance();
        List<ICommand> commands = new ArrayList<>(Arrays.asList(
                new HelpCommand(expensive),
                new DebugCommand(),
                new ConfigCommand(expensive),
                new MacroCommand(expensive),
                new BindCommand(expensive),
                new FriendCommand()));
        return Collections.unmodifiableList(commands);
    }
}
