package ru.expensive.implement.features.modules.movement;

import ru.expensive.api.event.EventHandler;
import ru.expensive.api.feature.module.Module;
import ru.expensive.api.feature.module.ModuleCategory;
import ru.expensive.common.util.coroutine.CoroutineContext;
import ru.expensive.implement.events.player.TickEvent;

public class FlightModule extends Module {

    CoroutineContext context = new CoroutineContext();

    public FlightModule() {
        super("Flight", ModuleCategory.MOVEMENT);
    }

    private static void suspend(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        context.launch(() -> {
            System.out.println("Coroutine 1 started");
            suspend(10000);
            System.out.println("Coroutine 1 resumed after 1 second");
        });
    }

    public void deactivate() {
        context.shutdown();
        super.deactivate();
    }
}