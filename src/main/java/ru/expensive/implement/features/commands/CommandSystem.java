package ru.expensive.implement.features.commands;

import ru.expensive.api.feature.command.ICommandSystem;
import ru.expensive.api.feature.command.argparser.IArgParserManager;
import ru.expensive.implement.features.commands.argparser.ArgParserManager;

public enum CommandSystem implements ICommandSystem {
    INSTANCE;

    @Override
    public IArgParserManager getParserManager() {
        return ArgParserManager.INSTANCE;
    }
}
