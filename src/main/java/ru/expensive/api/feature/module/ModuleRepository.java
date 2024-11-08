package ru.expensive.api.feature.module;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.implement.features.modules.combat.*;
import ru.expensive.implement.features.modules.misc.ServerRPSpooferModule;
import ru.expensive.implement.features.modules.movement.AutoSprintModule;
import ru.expensive.implement.features.modules.movement.FlightModule;
import ru.expensive.implement.features.modules.player.*;
import ru.expensive.implement.features.modules.render.AuctionHelperModule;
import ru.expensive.implement.features.modules.render.HudModule;
import ru.expensive.implement.features.modules.render.PearlPredictionModule;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ModuleRepository {
    List<Module> modules = new ArrayList<>();

    public void setup() {
        register(
                new HudModule(),
                new AuctionHelperModule(),
                new PearlPredictionModule(),
                new AuraModule(),
                new AntiBot(),
                new FlightModule(),
                new NoFriendDamageModule(),
                new HitBoxModule(),
                new AutoSwapModule(),
                new AutoSprintModule(),
                new NoPushModule(),
                new ClickPearlModule(),
                new NoDelayModule(),
                new AutoRespawnModule(),
                new ScreenWalkModule(),
                new ServerRPSpooferModule());
    }

    public void register(Module... module) {
        modules.addAll(List.of(module));
    }

    public List<Module> modules() {
        return modules;
    }
}
