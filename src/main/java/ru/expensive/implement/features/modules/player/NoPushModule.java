package ru.expensive.implement.features.modules.player;

import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.implement.events.block.PushBlockEvent;
import ru.expensive.implement.events.block.PushPlayerEvent;
import ru.expensive.implement.events.block.PushWaterEvent;
import ru.expensive.implement.events.block.ShouldBlockVision;

public class NoPushModule extends Module {
    public NoPushModule() {
        super("NoPush", "No Push", ModuleCategory.PLAYER);
    }

    @EventHandler
    public void onPushPlayer(PushPlayerEvent pushPlayerEvent) {
        pushPlayerEvent.cancel();
    }

    @EventHandler
    public void onPushBlock(PushBlockEvent pushBlockEvent) {
        pushBlockEvent.cancel();
    }

    @EventHandler
    public void onBlockVision(ShouldBlockVision pushWaterEvent) {
        pushWaterEvent.cancel();
    }

    @EventHandler
    public void onPushWater(PushWaterEvent pushWaterEvent) {
        pushWaterEvent.cancel();
    }
}
