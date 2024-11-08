package ru.expensive.api.repository.macro;

import net.minecraft.util.Formatting;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.event.EventManager;
import ru.expensive.common.QuickImports;
import ru.expensive.common.QuickLogger;
import ru.expensive.implement.events.keyboard.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class MacroRepository implements QuickImports, QuickLogger {
    public List<Macro> macroList = new ArrayList<>();

    public MacroRepository(EventManager eventManager) {
        eventManager.register(this);
    }

    public boolean isEmpty() {
        return macroList.isEmpty();
    }

    public void addMacro(String name, String message, int key) {
        macroList.add(new Macro(name, message, key));
    }

    public boolean hasMacro(String macroName) {
        for (Macro macro : macroList) {
            if (macro.getName().equalsIgnoreCase(macroName)) {
                return true;
            }
        }
        return false;
    }

    public void deleteMacro(String name) {
        if (macroList.stream()
                .anyMatch(macro -> macro.getName().equals(name))) {
            macroList.removeIf(macro -> macro.getName().equalsIgnoreCase(name));
        }
    }

    public void clearList() {
        if (!macroList.isEmpty()) {
            macroList.clear();
        }
    }

    @EventHandler
    public void onKey(KeyEvent event) {
        int key = event.getKey();
        if (mc.player == null || event.getAction() != 0) {
            return;
        }
        macroList.stream()
                .filter(macro -> macro.getKey() == key)
                .findFirst()
                .ifPresent(macro -> {
                    try {
                        mc.getNetworkHandler().sendChatMessage(macro.getMessage());
                    } catch (Exception e) {
                        logDirect("Ошибка при отправки команды " + e, Formatting.RED);
                    }
                });
    }
}