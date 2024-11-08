package ru.expensive.api.feature.command.datatypes;

import ru.expensive.api.feature.command.argument.IArgConsumer;

public interface IDatatypeContext {
    IArgConsumer getConsumer();
}
