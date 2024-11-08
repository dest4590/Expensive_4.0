package ru.expensive.implement.features.modules.combat;

import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.api.feature.module.setting.implement.BindSetting;
import ru.expensive.api.feature.module.setting.implement.SelectSetting;

public class AutoSwapModule extends Module {

    public static final SelectSetting firstItem = new SelectSetting("First item", "Select first swap item.")
            .value("Totem of Undying", "Head", "Talisman", "Golden Apple", "Shield");
    public static final SelectSetting secondItem = new SelectSetting("Second item", "Select second swap item.")
            .value("Totem of Undying", "Head", "Talisman", "Golden Apple", "Shield");
    private final BindSetting bindSetting = new BindSetting("Item use key", "Uses item when pressed");

    public AutoSwapModule() {
        super("AutoSwap", "Auto Swap", ModuleCategory.COMBAT);
        setup(firstItem, secondItem, bindSetting);
    }
}
