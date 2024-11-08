package ru.expensive.implement.features.commands.defaults;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.expensive.api.feature.command.Command;
import ru.expensive.api.feature.command.argument.IArgConsumer;
import ru.expensive.api.feature.command.datatypes.KeyDataType;
import ru.expensive.api.feature.command.datatypes.MacroDataType;
import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.helpers.Paginator;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.api.repository.macro.MacroRepository;
import ru.expensive.common.util.other.StringUtil;
import ru.expensive.core.Expensive;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static ru.expensive.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MacroCommand extends Command {

    final MacroRepository macroRepository;

    public MacroCommand(Expensive expensive) {
        super("macro", "macros");
        macroRepository = expensive.getMacroRepository();
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String action = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        switch (action) {
            case "add" -> handleAddMacro(args);
            case "remove" -> handleRemoveMacro(args);
            case "list" -> handleListMacros(args, label);
            case "clear" -> handleClearMacros(args);
        }
    }

    private void handleAddMacro(IArgConsumer args) throws CommandException {
        args.requireMin(3);
        int key = args.getDatatypeFor(KeyDataType.INSTANCE).getValue();
        String name = args.getString();
        String command = args.rawRest();

        if (macroRepository.hasMacro(name)) {
            logDirect("Макрос с таким именем уже есть в списке!", Formatting.RED);
            return;
        }

        macroRepository.addMacro(name, command, key);
        logDirect(Formatting.GREEN +
                "Добавлен макрос с названием " + Formatting.RED
                + name + Formatting.GREEN
                + " с кнопкой " + Formatting.RED
                + StringUtil.getBindName(key).toLowerCase() + Formatting.GREEN
                + " с командой " + Formatting.RED
                + command);
    }

    private void handleRemoveMacro(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        String name = args.getString();
        if (macroRepository.hasMacro(name)) {
            macroRepository.deleteMacro(name);
            logDirect(Formatting.GREEN + "Макрос " + Formatting.RED + name + Formatting.GREEN + " был успешно удален!");
        } else {
            logDirect("Макрос с таким именем не найден!", Formatting.RED);
        }
    }

    private void handleListMacros(IArgConsumer args, String label) throws CommandException {
        args.requireMax(1);
        Paginator.paginate(
                args, new Paginator<>(macroRepository.macroList),
                () -> logDirect("Список макросов:"),
                macro -> {
                    String names = macro.getName();
                    String keys = StringUtil.getBindName(macro.getKey()).toLowerCase();
                    String command = macro.getMessage();

                    return Text.literal(Formatting.GRAY + "Название: " + Formatting.WHITE + names)
                            .append(Text.literal(Formatting.GRAY + " Клавиша: " + Formatting.WHITE + keys))
                            .append(Text.literal(Formatting.GRAY + " Команда: " + Formatting.WHITE + command));
                },
                FORCE_COMMAND_PREFIX + label);
    }

    private void handleClearMacros(IArgConsumer args) throws CommandException {
        args.requireMax(1);
        macroRepository.clearList();
        logDirect("Все макросы были удалены.", Formatting.GREEN);
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny() && args.hasExactlyOne()) {
            return new TabCompleteHelper()
                    .sortAlphabetically()
                    .prepend("add", "remove", "list", "clear")
                    .filterPrefix(args.getString())
                    .stream();
        } else if (args.hasAny()) {
            String arg = args.getString();
            if (arg.equalsIgnoreCase("add") && args.hasExactlyOne()) {
                return args.tabCompleteDatatype(KeyDataType.INSTANCE);
            } else if (arg.equalsIgnoreCase("remove") && args.hasExactlyOne()) {
                return args.tabCompleteDatatype(MacroDataType.INSTANCE);
            }
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Позволяет управлять макросами";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Эта команда позволяет управлять макросами, которые автоматически вводят заданные команды в чат.",
                "",
                "Использование:",
                "> macro add <key> <name> <message> - Добавляет новый макрос, который будет активироваться при нажатии на указанную клавишу и вводить указанное сообщение.",
                "> macro remove <name> - Удаляет макрос с указанным именем.",
                "> macro list - Отображает список всех текущих макросов.",
                "> macro clear - Удаляет все макросы из списка.");
    }

}
