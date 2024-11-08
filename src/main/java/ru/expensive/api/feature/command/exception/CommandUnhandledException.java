package ru.expensive.api.feature.command.exception;

import ru.expensive.api.feature.command.ICommand;
import ru.expensive.api.feature.command.argument.ICommandArgument;
import ru.expensive.common.QuickLogger;

import java.util.List;

public class CommandUnhandledException extends RuntimeException implements ICommandException, QuickLogger {

    public CommandUnhandledException(String message) {
        super(message);
    }

    public CommandUnhandledException(Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
    }
}
