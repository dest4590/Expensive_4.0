package ru.expensive.api.feature.module.setting.implement;

import lombok.Getter;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.feature.module.setting.Setting;
import ru.expensive.implement.events.setting.SettingsUpdateEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
public class SelectSetting extends Setting {
    private String selected;
    private List<String> list;

    public SelectSetting(String name, String description) {
        super(name, description);
    }

    public SelectSetting value(String... values) {
        List<String> list = Arrays.asList(values);

        selected = list.get(0);
        this.list = list;

        return this;
    }

    public SelectSetting visible(Supplier<Boolean> visible) {
        setVisible(visible);
        return this;
    }

    public boolean isSelected(String name) {
        return selected.equalsIgnoreCase(name);
    }

    public void setSelected(String selected) {
        this.selected = selected;
        // Костыль ебаный фикси
        if (getName().equalsIgnoreCase("AntiCheat")) {
            EventManager.callEvent(new SettingsUpdateEvent());
        }
    }
}
