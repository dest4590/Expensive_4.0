package ru.expensive.implement.features.commands.manager;

import net.minecraft.util.Pair;
import ru.expensive.api.feature.command.ICommand;
import ru.expensive.api.feature.command.argument.ICommandArgument;
import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.exception.CommandUnhandledException;
import ru.expensive.api.feature.command.exception.ICommandException;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.api.feature.command.manager.ICommandManager;
import ru.expensive.api.feature.command.registry.Registry;
import ru.expensive.core.Expensive;
import ru.expensive.implement.features.commands.argument.ArgConsumer;
import ru.expensive.implement.features.commands.argument.CommandArguments;
import ru.expensive.implement.features.commands.defaults.DefaultCommands;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class CommandRepository implements ICommandManager {

    private final Registry<ICommand> registry = new Registry<>();

    public CommandRepository() {
        DefaultCommands.createAll().forEach(this.registry::register);
    }

    private static Pair<String, List<ICommandArgument>> expand(String string, boolean preserveEmptyLast) {
        String label = string.split("\\s", 2)[0];
        List<ICommandArgument> args = CommandArguments.from(string.substring(label.length()), preserveEmptyLast);
        return new Pair<>(label, args);
    }

    public static Pair<String, List<ICommandArgument>> expand(String string) {
        return expand(string, false);
    }

    @Override
    public Registry<ICommand> getRegistry() {
        return this.registry;
    }

    @Override
    public ICommand getCommand(String name) {
        for (ICommand command : this.registry.entries) {
            if (command.getNames().contains(name.toLowerCase(Locale.US))) {
                return command;
            }
        }
        return null;
    }

    @Override
    public boolean execute(String string) {
        return this.execute(expand(string));
    }

    @Override
    public boolean execute(Pair<String, List<ICommandArgument>> expanded) {
        ExecutionWrapper execution = this.from(expanded);
        if (execution != null) {
            execution.execute();
        }
        return execution != null;
    }

    @Override
    public Stream<String> tabComplete(Pair<String, List<ICommandArgument>> expanded) {
        ExecutionWrapper execution = this.from(expanded);
        return execution == null ? Stream.empty() : execution.tabComplete();
    }

    @Override
    public Stream<String> tabComplete(String prefix) {
        Pair<String, List<ICommandArgument>> pair = expand(prefix, true);
        String label = pair.getLeft();
        List<ICommandArgument> args = pair.getRight();
        if (args.isEmpty()) {
            return new TabCompleteHelper()
                    .addCommands(Expensive.getInstance().getCommandRepository())
                    .filterPrefix(label)
                    .stream();
        } else {
            return tabComplete(pair);
        }
    }

    private ExecutionWrapper from(Pair<String, List<ICommandArgument>> expanded) {
        String label = expanded.getLeft();
        ArgConsumer args = new ArgConsumer(this, expanded.getRight());

        ICommand command = this.getCommand(label);
        return command == null ? null : new ExecutionWrapper(command, label, args);
    }

    private static final class ExecutionWrapper {

        private final ICommand command;
        private final String label;
        private final ArgConsumer args;

        private ExecutionWrapper(ICommand command, String label, ArgConsumer args) {
            this.command = command;
            this.label = label;
            this.args = args;
        }

        private void execute() {
            try {
                this.command.execute(this.label, this.args);
            } catch (Throwable t) {
                // Create a handleable exception, wrap if needed
                ICommandException exception = t instanceof ICommandException
                        ? (ICommandException) t
                        : new CommandUnhandledException(t);

                exception.handle(command, args.getArgs());
            }
        }

        private Stream<String> tabComplete() {
            try {
                return this.command.tabComplete(this.label, this.args);
            } catch (CommandException ignored) {
                // NOP
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return Stream.empty();
        }
    }
}
