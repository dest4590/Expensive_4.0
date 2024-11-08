package ru.expensive.api.feature.module;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.Formatting;
import ru.expensive.api.event.EventHandler;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.feature.module.exception.ModuleException;
import ru.expensive.api.system.logger.implement.ConsoleLogger;
import ru.expensive.common.QuickLogger;
import ru.expensive.implement.events.keyboard.KeyEvent;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleSwitcher implements QuickLogger {
    List<Module> modules;

    public ModuleSwitcher(List<Module> modules, EventManager eventManager) {
        this.modules = modules;
        eventManager.register(this);
    }

    @EventHandler
    public void onKey(KeyEvent event) {
        for (Module module : modules) {
            if (event.getKey() == module.getKey()) {
                try {
                    handleModuleState(module, event.getAction());
                } catch (Exception e) {
                    handleException(module.getName(), e);
                }
                break;
            }
        }
    }

    private void handleModuleState(Module module, int action) {
        // Если тип модуля - toggle и действие - press
        if (module.getType() == 1 && action == 0) {
            module.switchState();
        }
        // Если тип модуля - hold и действие - press или release
        else if (module.getType() == 0 && (action == 0 || action == 1)) {
            module.switchState();
        }
    }

    private void handleException(String moduleName, Exception e) {
        final ConsoleLogger consoleLogger = new ConsoleLogger();

        if (e instanceof ModuleException) {
            logDirect("[" + moduleName + "] " + Formatting.RED + e.getMessage());
        } else {
            consoleLogger.log("Error in module " + moduleName + ": " + e.getMessage());
        }
    }
}
