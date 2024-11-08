package ru.expensive.api.feature.command.datatypes;

import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.api.feature.module.Module;
import ru.expensive.core.Expensive;

import java.util.List;
import java.util.stream.Stream;

public enum ModuleDataType implements IDatatypeFor<Module> {
    INSTANCE;

    @Override
    public Stream<String> tabComplete(IDatatypeContext datatypeContext) throws CommandException {
        Stream<String> source = getModules()
                .stream()
                .map(Module::getName);

        String context = datatypeContext
                .getConsumer()
                .getString();

        return new TabCompleteHelper()
                .append(source)
                .filterPrefix(context)
                .sortAlphabetically()
                .stream();
    }

    @Override
    public Module get(IDatatypeContext datatypeContext) throws CommandException {
        final String name = datatypeContext.getConsumer().getString();
        return getModules().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    private List<? extends Module> getModules() {
        return Expensive.getInstance().getModuleRepository().modules();
    }
}
