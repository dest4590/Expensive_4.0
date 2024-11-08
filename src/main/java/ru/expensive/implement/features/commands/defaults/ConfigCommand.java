package ru.expensive.implement.features.commands.defaults;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.expensive.api.feature.command.Command;
import ru.expensive.api.feature.command.argument.IArgConsumer;
import ru.expensive.api.feature.command.datatypes.ConfigFileDataType;
import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.helpers.Paginator;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.api.file.FileController;
import ru.expensive.api.file.exception.FileProcessingException;
import ru.expensive.core.Expensive;
import ru.expensive.core.client.ClientInfoProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static ru.expensive.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigCommand extends Command {
    FileController fileController;
    ClientInfoProvider clientInfoProvider;

    protected ConfigCommand(Expensive expensive) {
        super("config", "cfg");
        this.fileController = expensive.getFileController();
        this.clientInfoProvider = expensive.getClientInfoProvider();
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        args.requireMax(1);
        if (arg.contains("load")) {
            String name = args.getString();
            if (new File(clientInfoProvider.configsDir(), name + ".json").exists()) {
                try {
                    fileController.loadFile(name + ".json");
                    logDirect(String.format("Конфигурация %s загружена!", name));
                } catch (FileProcessingException e) {
                    logDirect(String.format("Ошибка при загрузке конфига! Детали: %s", e.getCause().getMessage()),
                            Formatting.RED);
                }
            } else {
                logDirect(String.format("Конфигурация %s не найдена!", name));
            }
        }
        if (arg.contains("save")) {

            String name = args.getString();

            try {
                fileController.saveFile(name + ".json");
                logDirect(String.format("Конфигурация %s сохранена!", name));
            } catch (Exception e) {
                logDirect(String.format("Ошибка при сохранении конфига! Детали: %s", e.getCause().getMessage()),
                        Formatting.RED);
            }
        }
        if (arg.contains("list")) {
            Paginator.paginate(
                    args, new Paginator<>(
                            getConfigs()),
                    () -> logDirect("Список конфигов:"),
                    config -> {
                        MutableText namesComponent = Text.literal(config);
                        namesComponent.setStyle(namesComponent.getStyle().withColor(Formatting.WHITE));
                        return namesComponent;
                    },
                    FORCE_COMMAND_PREFIX + label);
        }
        if (arg.contains("dir")) {
            try {
                Runtime.getRuntime().exec("explorer " + clientInfoProvider.configsDir().getAbsolutePath());
            } catch (IOException e) {
                logDirect("Папка с конфигурациями не найдена!" + e.getMessage());
            }
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (args.hasExactlyOne()) {
                if (arg.equalsIgnoreCase("load")) {
                    return args.tabCompleteDatatype(ConfigFileDataType.INSTANCE);
                } else if (arg.equalsIgnoreCase("save")) {
                    return args.tabCompleteDatatype(ConfigFileDataType.INSTANCE);
                }
            } else {
                return new TabCompleteHelper()
                        .sortAlphabetically()
                        .prepend("load", "save", "list", "dir")
                        .filterPrefix(arg)
                        .stream();
            }
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Позволяет взаимодействовать с конфигами в чите";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "С помощью этой команды можно загружать/сохранять конфиги",
                "",
                "Использование:",
                "> config load <name> - Загружает конфиг.",
                "> config save <name> - Сохраняет конфиг.",
                "> config list - Возвращает список конфигов",
                "> config dir - Открывает папку с конфигами.");
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
