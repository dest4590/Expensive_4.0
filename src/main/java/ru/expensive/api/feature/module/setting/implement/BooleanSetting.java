package ru.expensive.api.feature.module.setting.implement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.lwjgl.glfw.GLFW;
import ru.expensive.api.feature.module.setting.Setting;

import java.util.function.Supplier;

@Getter
@Setter
@Accessors(chain = true)
public class BooleanSetting extends Setting {
    private boolean value;
    private int key = GLFW.GLFW_KEY_UNKNOWN;
    private int type = 1; // 0 - hold, 1 - toggle

    public BooleanSetting(String name, String description) {
        super(name, description);
    }

    public BooleanSetting visible(Supplier<Boolean> visible) {
        setVisible(visible);
        return this;
    }
}