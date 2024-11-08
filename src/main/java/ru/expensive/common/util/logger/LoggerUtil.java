package ru.expensive.common.util.logger;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class LoggerUtil {
    public static void info(Object message) {
        log.info(ConsoleColors.BLACK + ConsoleColors.BG_GREEN + message + ConsoleColors.RESET);
    }

    public static void info(Object message, Throwable exception) {
        log.info(ConsoleColors.BLACK + ConsoleColors.BG_GREEN + message + ConsoleColors.RESET, exception);
    }

    public static void info(Object message, Object o) {
        log.info(ConsoleColors.BLACK + ConsoleColors.BG_GREEN + message + ConsoleColors.RESET, o);
    }

    public static void warn(Object message) {
        log.warn(ConsoleColors.BLACK + ConsoleColors.BG_YELLOW + message + ConsoleColors.RESET);
    }

    public static void warn(Object message, Throwable exception) {
        log.warn(ConsoleColors.BLACK + ConsoleColors.BG_YELLOW + message + ConsoleColors.RESET, exception);
    }

    public static void warn(Object message, Object o) {
        log.warn(ConsoleColors.BLACK + ConsoleColors.BG_YELLOW + message + ConsoleColors.RESET, o);
    }

    public static void error(Object message) {
        log.error(ConsoleColors.BLACK + ConsoleColors.BG_RED + message + ConsoleColors.RESET);
    }

    public static void error(Object message, Throwable exception) {
        log.error(ConsoleColors.BLACK + ConsoleColors.BG_RED + message + ConsoleColors.RESET, exception);
    }

    public void error(Object message, Object o) {
        log.error(ConsoleColors.BLACK + ConsoleColors.BG_RED + message + ConsoleColors.RESET, o);
    }
}
