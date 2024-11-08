package ru.expensive.api.event;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ru.expensive.api.event.events.Event;
import ru.expensive.api.event.events.EventStoppable;
import ru.expensive.api.event.types.Priority;
import ru.expensive.api.feature.module.exception.ModuleException;
import ru.expensive.api.system.logger.implement.ConsoleLogger;
import ru.expensive.api.system.logger.implement.MinecraftLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventManager {
    private static final Map<Class<? extends Event>, List<MethodData>> REGISTRY_MAP = new HashMap<>();

    public EventManager() {
    }

    public static void cleanMap(boolean onlyEmptyEntries) {
        Iterator<Map.Entry<Class<? extends Event>, List<MethodData>>> mapIterator = REGISTRY_MAP.entrySet().iterator();

        while (mapIterator.hasNext()) {
            if (!onlyEmptyEntries || mapIterator.next().getValue().isEmpty()) {
                mapIterator.remove();
            }
        }
    }

    private static void sortListValue(Class<? extends Event> indexClass) {
        List<MethodData> sortedList = new CopyOnWriteArrayList<MethodData>();

        for (final byte priority : Priority.VALUE_ARRAY) {
            for (final MethodData data : REGISTRY_MAP.get(indexClass)) {
                if (data.getPriority() == priority) {
                    sortedList.add(data);
                }
            }
        }

        REGISTRY_MAP.put(indexClass, sortedList);
    }

    private static boolean isMethodBad(Method method) {
        return method.getParameterTypes().length != 1 || !method.isAnnotationPresent(EventHandler.class);
    }

    private static boolean isMethodBad(Method method, Class<? extends Event> eventClass) {
        return isMethodBad(method) || !method.getParameterTypes()[0].equals(eventClass);
    }

    public static Event callEvent(final Event event) {
        List<MethodData> dataList = REGISTRY_MAP.get(event.getClass());

        if (dataList != null) {
            if (event instanceof EventStoppable stoppable) {

                for (final MethodData data : dataList) {
                    invoke(data, event);

                    if (stoppable.isStopped()) {
                        break;
                    }
                }
            } else {
                for (final MethodData data : dataList) {
                    try {
                        invoke(data, event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return event;
    }

    private static void invoke(MethodData data, Event argument) {
        try {
            data.getTarget().invoke(data.getSource(), argument);
        } catch (IllegalAccessException e) {
            ConsoleLogger consoleLogger = new ConsoleLogger();
            String errorMessage = "Illegal access to method. ";
            errorMessage += "Method: " + data.getTarget().getName() + ", ";
            errorMessage += "Argument: " + argument.toString() + ", ";
            errorMessage += "Log: " + e.fillInStackTrace();
            consoleLogger.log(errorMessage);
        } catch (IllegalArgumentException e) {
            ConsoleLogger consoleLogger = new ConsoleLogger();
            String errorMessage = "Illegal arguments passed to method. ";
            errorMessage += "Method: " + data.getTarget().getName() + ", ";
            errorMessage += "Argument: " + argument.toString() + ", ";
            errorMessage += "Log: " + e.getCause();
            consoleLogger.log(errorMessage);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            ConsoleLogger consoleLogger = new ConsoleLogger();
            MinecraftLogger minecraftLogger = new MinecraftLogger();
            if (cause instanceof ModuleException moduleException) {
                minecraftLogger.minecraftLog(Text.literal(
                        "[" + moduleException.getModuleName() + "] " + Formatting.RED + moduleException.getMessage()));
            } else {
                String errorMessage = "Exception occurred within invoked method. ";
                errorMessage += "Method: " + data.getTarget().getName() + ", ";
                errorMessage += "Argument: " + argument.toString() + ", ";
                errorMessage += "Log: " + e.getCause();
                consoleLogger.log(errorMessage);
            }
        }
    }

    public void register(Object object) {
        for (final Method method : object.getClass().getDeclaredMethods()) {
            if (isMethodBad(method)) {
                continue;
            }

            register(method, object);
        }
    }

    public void register(Object object, Class<? extends Event> eventClass) {
        for (final Method method : object.getClass().getDeclaredMethods()) {
            if (isMethodBad(method, eventClass)) {
                continue;
            }

            register(method, object);
        }

    }

    public void unregister(Object object) {
        for (final List<MethodData> dataList : REGISTRY_MAP.values()) {
            for (final MethodData data : dataList) {
                if (data.getSource().equals(object)) {
                    dataList.remove(data);
                }
            }
        }

        cleanMap(true);
    }

    public void unregister(Object object, Class<? extends Event> eventClass) {
        if (REGISTRY_MAP.containsKey(eventClass)) {
            for (final MethodData data : REGISTRY_MAP.get(eventClass)) {
                if (data.getSource().equals(object)) {
                    REGISTRY_MAP.get(eventClass).remove(data);
                }
            }

            cleanMap(true);
        }
    }

    private void register(Method method, Object object) {
        Class<? extends Event> indexClass = (Class<? extends Event>) method.getParameterTypes()[0];
        final MethodData data = new MethodData(object, method, method.getAnnotation(EventHandler.class).value());

        if (!data.getTarget().isAccessible()) {
            data.getTarget().setAccessible(true);
        }

        if (REGISTRY_MAP.containsKey(indexClass)) {
            if (!REGISTRY_MAP.get(indexClass).contains(data)) {
                REGISTRY_MAP.get(indexClass).add(data);
                sortListValue(indexClass);
            }
        } else {
            REGISTRY_MAP.put(indexClass, new CopyOnWriteArrayList<MethodData>() {
                private static final long serialVersionUID = 666L;

                {
                    add(data);
                }
            });
        }
    }

    public void removeEntry(Class<? extends Event> indexClass) {
        Iterator<Map.Entry<Class<? extends Event>, List<MethodData>>> mapIterator = REGISTRY_MAP.entrySet().iterator();

        while (mapIterator.hasNext()) {
            if (mapIterator.next().getKey().equals(indexClass)) {
                mapIterator.remove();
                break;
            }
        }
    }

    private static final class MethodData {

        private final Object source;

        private final Method target;

        private final byte priority;

        public MethodData(Object source, Method target, byte priority) {
            this.source = source;
            this.target = target;
            this.priority = priority;
        }

        public Object getSource() {
            return source;
        }

        public Method getTarget() {
            return target;
        }

        public byte getPriority() {
            return priority;
        }

    }
}
