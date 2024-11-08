package ru.expensive.api.feature.command.datatypes;

import ru.expensive.api.feature.command.exception.CommandException;

public interface IDatatypeFor<T> extends IDatatype {
    T get(IDatatypeContext datatypeContext) throws CommandException;
}
