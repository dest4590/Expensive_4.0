package ru.expensive.api.feature.module.setting.implement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.expensive.api.feature.module.setting.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
@Setter
@Accessors(chain = true)
public class GroupSetting extends Setting {
    private boolean value;
    private List<Setting> subSettings = new ArrayList<>();

    public GroupSetting(String name, String description) {
        super(name, description);
    }

    public GroupSetting settings(Setting... setting) {
        subSettings.addAll(Arrays.asList(setting));
        return this;
    }

    public GroupSetting visible(Supplier<Boolean> visible) {
        setVisible(visible);
        return this;
    }

    public Setting getSubSetting(String name) {
        return subSettings.stream()
                .filter(setting -> setting.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}