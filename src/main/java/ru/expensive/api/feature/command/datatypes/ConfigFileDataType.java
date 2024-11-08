package ru.expensive.api.feature.command.datatypes;

import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.core.Expensive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public enum ConfigFileDataType implements IDatatypeFor<String> {
    INSTANCE;

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        Stream<String> friends = getConfigs()
                .stream()
                .map(String::toString);

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
    public String get(IDatatypeContext datatypeContext) throws CommandException {
        String username = datatypeContext
                .getConsumer()
                .getString();

        return getConfigs().stream()
                .filter(s -> s.equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    public List<String> getConfigs() {
        List<String> configs = new ArrayList<>();
        File[] configFiles = Expensive.getInstance().getClientInfoProvider().configsDir().listFiles();

        if (configFiles != null) {
            for (File configFile : configFiles) {
                if (configFile.isFile() && configFile.getName().endsWith(".json")) {
                    String configName = configFile.getName().replace(".json", "");
                    configs.add(configName);
                }
            }
        }

        return configs;
    }
}
