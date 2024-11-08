package ru.expensive.common.trait;

import ru.expensive.api.feature.module.setting.Setting;

public interface Setupable {
    void setup(Setting... settings);
}