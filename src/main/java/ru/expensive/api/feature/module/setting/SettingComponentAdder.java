package ru.expensive.api.feature.module.setting;

import ru.expensive.api.feature.module.setting.implement.*;
import ru.expensive.implement.screens.menu.components.implement.settings.*;
import ru.expensive.implement.screens.menu.components.implement.settings.multiselect.MultiSelectComponent;
import ru.expensive.implement.screens.menu.components.implement.settings.select.SelectComponent;

import java.util.List;

public class SettingComponentAdder {
    public void addSettingComponent(List<Setting> settings, List<AbstractSettingComponent> components) {
        settings.forEach(setting -> {
            if (setting instanceof BooleanSetting booleanSetting) {
                components.add(new CheckboxComponent(booleanSetting));
            }

            if (setting instanceof BindSetting bindSetting) {
                components.add(new BindComponent(bindSetting));
            }

            if (setting instanceof ColorSetting colorSetting) {
                components.add(new ColorComponent(colorSetting));
            }

            if (setting instanceof TextSetting textSetting) {
                components.add(new TextComponent(textSetting));
            }

            if (setting instanceof ValueSetting valueSetting) {
                components.add(new ValueComponent(valueSetting));
            }

            if (setting instanceof GroupSetting groupSetting) {
                components.add(new GroupComponent(groupSetting));
            }

            if (setting instanceof ButtonSetting buttonSetting) {
                components.add(new SButtonComponent(buttonSetting));
            }

            if (setting instanceof SelectSetting selectSetting) {
                components.add(new SelectComponent(selectSetting));
            }

            if (setting instanceof MultiSelectSetting multiSelectSetting) {
                components.add(new MultiSelectComponent(multiSelectSetting));
            }
        });
    }
}
