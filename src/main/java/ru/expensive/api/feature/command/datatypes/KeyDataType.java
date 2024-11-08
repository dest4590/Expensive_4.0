package ru.expensive.api.feature.command.datatypes;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.util.InputUtil;
import ru.expensive.api.feature.command.exception.CommandException;
import ru.expensive.api.feature.command.helpers.TabCompleteHelper;
import ru.expensive.asm.mixins.accessors.TypeAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static ru.expensive.common.util.other.StringUtil.getBindName;

public enum KeyDataType implements IDatatypeFor<Map.Entry<String, Integer>> {
    INSTANCE;

    private static Map<String, Integer> getKeys() {
        Map<String, Integer> keys = new HashMap<>();
        Int2ObjectMap<InputUtil.Key> keyBindings = ((TypeAccessor) (Object) InputUtil.Type.KEYSYM).getMap();

        for (Int2ObjectMap.Entry<InputUtil.Key> entry : keyBindings.int2ObjectEntrySet()) {
            int keyCode = entry.getIntKey();
            String bindName = getBindName(keyCode).toLowerCase();
            keys.put(bindName, keyCode);
        }

        return keys;
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext datatypeContext) throws CommandException {
        Stream<String> keys = getKeys()
                .keySet()
                .stream();

        String context = datatypeContext
                .getConsumer()
                .getString();

        return new TabCompleteHelper()
                .append(keys)
                .filterPrefix(context)
                .sortAlphabetically()
                .stream();
    }

    @Override
    public Map.Entry<String, Integer> get(IDatatypeContext datatypeContext) throws CommandException {
        String key = datatypeContext
                .getConsumer()
                .getString();

        return getKeys()
                .entrySet()
                .stream()
                .filter(s -> s.getKey().equalsIgnoreCase(key))
                .findFirst()
                .orElse(null);
    }
}
