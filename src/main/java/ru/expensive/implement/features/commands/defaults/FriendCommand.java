package ru.expensive.implement.features.commands.defaults;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.expensive.api.feature.command.Command;
import ru.expensive.api.feature.command.argument.IArgConsumer;
import ru.expensive.api.feature.command.datatypes.FriendDataType;
import ru.expensive.api.feature.command.datatypes.TabPlayerDataType;
import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.helpers.Paginator;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.api.repository.friend.FriendRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static ru.expensive.api.feature.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

public class FriendCommand extends Command {
    protected FriendCommand() {
        super("friend");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        String arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        args.requireMax(1);
        if (arg.contains("add")) {
            String name = args.getString();
            if (!FriendRepository.isFriend(name)) {
                FriendRepository.addFriend(name);
                logDirect("Вы успешно добавили " + name + " в список друзей!");
            } else {
                logDirect(name + " уже есть в списке друзей!", Formatting.RED);
            }
        }
        if (arg.contains("remove")) {
            String name = args.getString();
            if (FriendRepository.isFriend(name)) {
                FriendRepository.removeFriend(name);
                logDirect("Вы успешно удалили " + name + " из списка друзей!");
                return;
            }
            logDirect(name + " не найден в списке друзей", Formatting.RED);
        }
        if (arg.contains("list")) {
            Paginator.paginate(
                    args, new Paginator<>(
                            FriendRepository.getFriends()),
                    () -> logDirect("Список друзей:"),
                    friend -> {
                        String names = friend.getName();
                        MutableText namesComponent = Text.literal(names);
                        namesComponent.setStyle(namesComponent.getStyle().withColor(Formatting.WHITE));
                        return namesComponent;
                    },
                    FORCE_COMMAND_PREFIX + label);
        }
        if (arg.contains("clear")) {
            FriendRepository.clear();
            logDirect("Список друзей очищен.");
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (args.hasExactlyOne()) {
                if (arg.equalsIgnoreCase("add")) {
                    return args.tabCompleteDatatype(TabPlayerDataType.INSTANCE);
                } else if (arg.equalsIgnoreCase("remove")) {
                    return args.tabCompleteDatatype(FriendDataType.INSTANCE);
                }
            } else {
                return new TabCompleteHelper()
                        .sortAlphabetically()
                        .prepend("add", "remove", "list", "clear")
                        .filterPrefix(arg)
                        .stream();
            }
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Позволяет управлять списком друзей";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "С помощью этой команды можно добавлять/удалять друзей в чите",
                "",
                "Использование:",
                "> friend add <name> - Добавляет имя в список друзей.",
                "> friend remove <name> - Удаляет имя из списка друзей.",
                "> friend list - Возвращает список друзей",
                "> friend clear - Очищает список друзей.");
    }
}
