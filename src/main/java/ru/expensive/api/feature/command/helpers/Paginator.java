package ru.expensive.api.feature.command.helpers;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.expensive.api.feature.command.argument.IArgConsumer;
import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.exception.CommandInvalidTypeException;
import ru.expensive.common.QuickLogger;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Paginator<E> implements QuickLogger {
    public final List<E> entries;
    public int pageSize = 8;
    public int page = 1;

    public Paginator(List<E> entries) {
        this.entries = entries;
    }

    @SafeVarargs
    public Paginator(E... entries) {
        this.entries = Arrays.asList(entries);
    }

    public static <T> void paginate(IArgConsumer consumer, Paginator<T> pagi, Runnable pre, Function<T, Text> transform,
                                    String commandPrefix) throws CommandException {
        int page = 1;
        consumer.requireMax(1);
        if (consumer.hasAny()) {
            page = consumer.getAs(Integer.class);
            if (!pagi.validPage(page)) {
                throw new CommandInvalidTypeException(
                        consumer.consumed(),
                        String.format(
                                "a valid page (1-%d)",
                                pagi.getMaxPage()),
                        consumer.consumed().getValue());
            }
        }
        pagi.skipPages(page - pagi.page);
        if (pre != null) {
            pre.run();
        }
        pagi.display(transform, commandPrefix);
    }

    public static <T> void paginate(IArgConsumer consumer, List<T> elems, Runnable pre, Function<T, Text> transform,
                                    String commandPrefix) throws CommandException {
        paginate(consumer, new Paginator<>(elems), pre, transform, commandPrefix);
    }

    public static <T> void paginate(IArgConsumer consumer, T[] elems, Runnable pre, Function<T, Text> transform,
                                    String commandPrefix) throws CommandException {
        paginate(consumer, Arrays.asList(elems), pre, transform, commandPrefix);
    }

    public static <T> void paginate(IArgConsumer consumer, Paginator<T> pagi, Function<T, Text> transform,
                                    String commandPrefix) throws CommandException {
        paginate(consumer, pagi, null, transform, commandPrefix);
    }

    public static <T> void paginate(IArgConsumer consumer, List<T> elems, Function<T, Text> transform,
                                    String commandPrefix) throws CommandException {
        paginate(consumer, new Paginator<>(elems), null, transform, commandPrefix);
    }

    public static <T> void paginate(IArgConsumer consumer, T[] elems, Function<T, Text> transform, String commandPrefix)
            throws CommandException {
        paginate(consumer, Arrays.asList(elems), null, transform, commandPrefix);
    }

    public static <T> void paginate(IArgConsumer consumer, Paginator<T> pagi, Runnable pre, Function<T, Text> transform)
            throws CommandException {
        paginate(consumer, pagi, pre, transform, null);
    }

    public static <T> void paginate(IArgConsumer consumer, List<T> elems, Runnable pre, Function<T, Text> transform)
            throws CommandException {
        paginate(consumer, new Paginator<>(elems), pre, transform, null);
    }

    public static <T> void paginate(IArgConsumer consumer, T[] elems, Runnable pre, Function<T, Text> transform)
            throws CommandException {
        paginate(consumer, Arrays.asList(elems), pre, transform, null);
    }

    public static <T> void paginate(IArgConsumer consumer, Paginator<T> pagi, Function<T, Text> transform)
            throws CommandException {
        paginate(consumer, pagi, null, transform, null);
    }

    public static <T> void paginate(IArgConsumer consumer, List<T> elems, Function<T, Text> transform)
            throws CommandException {
        paginate(consumer, new Paginator<>(elems), null, transform, null);
    }

    public static <T> void paginate(IArgConsumer consumer, T[] elems, Function<T, Text> transform)
            throws CommandException {
        paginate(consumer, Arrays.asList(elems), null, transform, null);
    }

    public Paginator<E> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public int getMaxPage() {
        return (entries.size() - 1) / pageSize + 1;
    }

    public boolean validPage(int page) {
        return page > 0 && page <= getMaxPage();
    }

    public void skipPages(int pages) {
        page += pages;
    }

    public void display(Function<E, Text> transform, String commandPrefix) {
        int offset = (page - 1) * pageSize;
        for (int i = offset; i < offset + pageSize; i++) {
            if (i < entries.size()) {
                logDirect(transform.apply(entries.get(i)));
            } else {
                logDirect("--", Formatting.DARK_GRAY);
            }
        }
        boolean hasPrevPage = commandPrefix != null && validPage(page - 1);
        boolean hasNextPage = commandPrefix != null && validPage(page + 1);
        MutableText prevPageComponent = Text.literal("<<");
        if (hasPrevPage) {
            prevPageComponent.setStyle(prevPageComponent.getStyle()
                    .withClickEvent(new ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            String.format("%s %d", commandPrefix, page - 1)))
                    .withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Text.literal("Click to view previous page"))));
        } else {
            prevPageComponent.setStyle(prevPageComponent.getStyle().withColor(Formatting.DARK_GRAY));
        }
        MutableText nextPageComponent = Text.literal(">>");
        if (hasNextPage) {
            nextPageComponent.setStyle(nextPageComponent.getStyle()
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            String.format("%s %d", commandPrefix, page + 1)))
                    .withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Text.literal("Click to view next page"))));
        } else {
            nextPageComponent.setStyle(nextPageComponent.getStyle().withColor(Formatting.DARK_GRAY));
        }
        MutableText pagerComponent = Text.literal("");
        pagerComponent.setStyle(pagerComponent.getStyle().withColor(Formatting.GRAY));
        pagerComponent.append(prevPageComponent);
        pagerComponent.append(" | ");
        pagerComponent.append(nextPageComponent);
        pagerComponent.append(String.format(" %d/%d", page, getMaxPage()));
        logDirect(pagerComponent);
    }

    public void display(Function<E, Text> transform) {
        display(transform, null);
    }
}
