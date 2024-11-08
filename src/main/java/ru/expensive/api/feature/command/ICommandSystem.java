package ru.expensive.api.feature.command;

import ru.expensive.api.feature.command.argparser.IArgParserManager;

public interface ICommandSystem {
    IArgParserManager getParserManager();
}
