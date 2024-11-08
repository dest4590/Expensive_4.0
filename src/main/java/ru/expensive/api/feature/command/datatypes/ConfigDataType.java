package ru.expensive.api.feature.command.datatypes;

import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.api.file.ClientFile;
import ru.expensive.api.file.impl.module.ModuleFile;
import ru.expensive.core.Expensive;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ConfigDataType implements IDatatypeFor<ClientFile> {
    INSTANCE;

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        Stream<String> friends = getList()
                .stream()
                .map(ModuleFile::getName);

        String context = ctx
                .getConsumer()
                .getString();

        return new TabCompleteHelper()
                .append(friends)
                .filterPrefix(context)
                .sortAlphabetically()
                .stream();
    }

    @Override
    public ClientFile get(IDatatypeContext datatypeContext) throws CommandException {
        String username = datatypeContext
                .getConsumer()
                .getString();

        return getList().stream()
                .filter(s -> s.getName().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public List<? extends ModuleFile> getList() {
        return Expensive.getInstance().getFileRepository().getClientFiles()
                .stream()
                .filter(clientFile -> clientFile instanceof ModuleFile)
                .map(clientFile -> (ModuleFile) clientFile)
                .collect(Collectors.toList());
    }
}
