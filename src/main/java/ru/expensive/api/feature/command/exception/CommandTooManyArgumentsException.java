package ru.expensive.api.feature.command.exception;

public class CommandTooManyArgumentsException extends CommandErrorMessageException {

    public CommandTooManyArgumentsException(int maxArgs) {
        super(String.format("Too many arguments (expected at most %d)", maxArgs));
    }
}
