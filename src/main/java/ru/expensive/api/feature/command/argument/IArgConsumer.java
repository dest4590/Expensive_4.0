package ru.expensive.api.feature.command.argument;

import ru.expensive.api.feature.command.datatypes.IDatatype;
import ru.expensive.api.feature.command.datatypes.IDatatypeFor;
import ru.expensive.api.feature.command.datatypes.IDatatypePost;
import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.exception.CommandInvalidTypeException;
import ru.expensive.api.feature.command.exception.CommandNotEnoughArgumentsException;
import ru.expensive.api.feature.command.exception.CommandTooManyArgumentsException;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Stream;

public interface IArgConsumer {
    LinkedList<ICommandArgument> getArgs();

    Deque<ICommandArgument> getConsumed();

    boolean has(int num);

    boolean hasAny();

    boolean hasAtMost(int num);

    boolean hasAtMostOne();

    boolean hasExactly(int num);

    boolean hasExactlyOne();

    ICommandArgument peek(int index) throws CommandNotEnoughArgumentsException;

    ICommandArgument peek() throws CommandNotEnoughArgumentsException;

    boolean is(Class<?> type, int index) throws CommandNotEnoughArgumentsException;

    boolean is(Class<?> type) throws CommandNotEnoughArgumentsException;

    String peekString(int index) throws CommandNotEnoughArgumentsException;

    String peekString() throws CommandNotEnoughArgumentsException;

    <E extends Enum<?>> E peekEnum(Class<E> enumClass, int index)
            throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <E extends Enum<?>> E peekEnum(Class<E> enumClass)
            throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <E extends Enum<?>> E peekEnumOrNull(Class<E> enumClass, int index) throws CommandNotEnoughArgumentsException;

    <E extends Enum<?>> E peekEnumOrNull(Class<E> enumClass) throws CommandNotEnoughArgumentsException;

    <T> T peekAs(Class<T> type, int index) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T> T peekAs(Class<T> type) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T> T peekAsOrDefault(Class<T> type, T def, int index) throws CommandNotEnoughArgumentsException;

    <T> T peekAsOrDefault(Class<T> type, T def) throws CommandNotEnoughArgumentsException;

    <T> T peekAsOrNull(Class<T> type, int index) throws CommandNotEnoughArgumentsException;

    <T> T peekAsOrNull(Class<T> type) throws CommandNotEnoughArgumentsException;

    <T> T peekDatatype(IDatatypeFor<T> datatype) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T, O> T peekDatatype(IDatatypePost<T, O> datatype)
            throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T, O> T peekDatatype(IDatatypePost<T, O> datatype, O original)
            throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T> T peekDatatypeOrNull(IDatatypeFor<T> datatype);

    <T, O> T peekDatatypeOrNull(IDatatypePost<T, O> datatype);

    <T, O, D extends IDatatypePost<T, O>> T peekDatatypePost(D datatype, O original)
            throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T, O, D extends IDatatypePost<T, O>> T peekDatatypePostOrDefault(D datatype, O original, T def);

    <T, O, D extends IDatatypePost<T, O>> T peekDatatypePostOrNull(D datatype, O original);

    <T, D extends IDatatypeFor<T>> T peekDatatypeFor(Class<D> datatype);

    <T, D extends IDatatypeFor<T>> T peekDatatypeForOrDefault(Class<D> datatype, T def);

    <T, D extends IDatatypeFor<T>> T peekDatatypeForOrNull(Class<D> datatype);

    ICommandArgument get() throws CommandNotEnoughArgumentsException;

    String getString() throws CommandNotEnoughArgumentsException;

    <E extends Enum<?>> E getEnum(Class<E> enumClass)
            throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <E extends Enum<?>> E getEnumOrDefault(Class<E> enumClass, E def) throws CommandNotEnoughArgumentsException;

    <E extends Enum<?>> E getEnumOrNull(Class<E> enumClass) throws CommandNotEnoughArgumentsException;

    <T> T getAs(Class<T> type) throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T> T getAsOrDefault(Class<T> type, T def) throws CommandNotEnoughArgumentsException;

    <T> T getAsOrNull(Class<T> type) throws CommandNotEnoughArgumentsException;

    <T, O, D extends IDatatypePost<T, O>> T getDatatypePost(D datatype, O original)
            throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T, O, D extends IDatatypePost<T, O>> T getDatatypePostOrDefault(D datatype, O original, T _default);

    <T, O, D extends IDatatypePost<T, O>> T getDatatypePostOrNull(D datatype, O original);

    <T, D extends IDatatypeFor<T>> T getDatatypeFor(D datatype)
            throws CommandInvalidTypeException, CommandNotEnoughArgumentsException;

    <T, D extends IDatatypeFor<T>> T getDatatypeForOrDefault(D datatype, T def);

    <T, D extends IDatatypeFor<T>> T getDatatypeForOrNull(D datatype);

    <T extends IDatatype> Stream<String> tabCompleteDatatype(T datatype);

    String rawRest();

    void requireMin(int min) throws CommandNotEnoughArgumentsException;

    void requireMax(int max) throws CommandTooManyArgumentsException;

    void requireExactly(int args) throws CommandException;

    boolean hasConsumed();

    ICommandArgument consumed();

    String consumedString();

    IArgConsumer copy();
}
