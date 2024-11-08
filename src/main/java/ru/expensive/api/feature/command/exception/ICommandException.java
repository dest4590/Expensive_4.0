package ru.expensive.api.feature.command.exception;

import net.minecraft.util.Formatting;
import ru.expensive.api.feature.command.ICommand;
import ru.expensive.api.feature.command.argument.ICommandArgument;
import ru.expensive.common.QuickLogger;

import java.util.List;

public interface ICommandException extends QuickLogger {

    String getMessage();

    default void handle(ICommand command, List<ICommandArgument> args) {
        logDirect(
                this.getMessage(),
                Formatting.RED);
    }
}
