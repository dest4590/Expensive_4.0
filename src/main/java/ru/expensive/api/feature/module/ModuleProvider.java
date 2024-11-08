package ru.expensive.api.feature.module;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.feature.module.exception.ModuleException;

import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleProvider {
    List<Module> modules;

    public Module module(String moduleName) {
        return modules.stream()
                .filter(module -> module.getName().equalsIgnoreCase(moduleName))
                .findFirst()
                .orElseThrow(() -> new ModuleException("Модуль " + moduleName + " не найден", moduleName));
    }
}
