package ru.expensive.api.feature.command.datatypes;

import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.common.QuickImports;

import java.util.stream.Stream;

public interface IDatatype extends QuickImports {
    Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException;
}
