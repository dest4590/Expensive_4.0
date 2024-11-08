package ru.expensive.implement.features.modules.render;

import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.implement.events.render.DrawEvent;

public class HudModule extends Module {

    public HudModule() {
        super("Hud", ModuleCategory.RENDER);
    }

    @EventHandler
    public void onDraw(DrawEvent drawEvent) {
    }
}
