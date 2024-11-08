package ru.expensive.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import ru.expensive.api.event.EventManager;
import ru.expensive.api.feature.draggable.DraggableRepository;
import ru.expensive.api.feature.module.ModuleProvider;
import ru.expensive.api.feature.module.ModuleRepository;
import ru.expensive.api.feature.module.ModuleSwitcher;
import ru.expensive.api.file.DirectoryCreator;
import ru.expensive.api.file.FileController;
import ru.expensive.api.file.FileRepository;
import ru.expensive.api.file.exception.FileProcessingException;
import ru.expensive.api.repository.macro.MacroRepository;
import ru.expensive.api.system.discord.DiscordManager;
import ru.expensive.api.system.localization.Language;
import ru.expensive.api.system.shader.ShadersPool;
import ru.expensive.common.util.logger.LoggerUtil;
import ru.expensive.common.util.render.ScissorManager;
import ru.expensive.core.client.ClientInfo;
import ru.expensive.core.client.ClientInfoProvider;
import ru.expensive.core.listener.ListenerRepository;
import ru.expensive.implement.events.setting.SettingsUpdateEvent;
import ru.expensive.implement.features.commands.CommandDispatcher;
import ru.expensive.implement.features.commands.manager.CommandRepository;
import ru.expensive.implement.features.modules.combat.killaura.attack.AttackPerpetrator;

import java.awt.*;
import java.io.File;
import java.net.URI;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class Expensive implements ModInitializer {
    @Getter
    static Expensive instance;
    AttackPerpetrator attackPerpetrator = new AttackPerpetrator();
    @NonFinal
    ModuleRepository moduleRepository;
    @NonFinal
    ModuleSwitcher moduleSwitcher;
    @NonFinal
    CommandRepository commandRepository;
    @NonFinal
    CommandDispatcher commandDispatcher;
    @NonFinal
    MacroRepository macroRepository;
    @NonFinal
    ModuleProvider moduleProvider;
    @NonFinal
    DraggableRepository draggableRepository;
    @NonFinal
    EventManager eventManager;
    @NonFinal
    DiscordManager discordManager;
    @NonFinal
    FileRepository fileRepository;
    @NonFinal
    FileController fileController;
    @NonFinal
    ScissorManager scissorManager;
    @NonFinal
    ClientInfoProvider clientInfoProvider;
    @NonFinal
    ListenerRepository listenerRepository;
    @NonFinal
    Language language = Language.ENG;
    @NonFinal
    boolean initialized = false;

    public static void openURL(String url) {
        try {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitialize() {
        instance = this;
        initClientInfoProvider();
        initEvents();
        initModules();
        initMacro();
        initFileManager();
        initCommands();
        initDraggable();
        initScissor();
        initListeners();
        ShadersPool.initShaders();
        EventManager.callEvent(new SettingsUpdateEvent());
        initDiscordRPC();

        initialized = true;
    }

    public void initEvents() {
        this.eventManager = new EventManager();
    }

    public void initDraggable() {
        draggableRepository = new DraggableRepository();
        draggableRepository.setup();
    }

    public void initModules() {
        moduleRepository = new ModuleRepository();
        moduleRepository.setup();
        moduleProvider = new ModuleProvider(moduleRepository.modules());
        moduleSwitcher = new ModuleSwitcher(moduleRepository.modules(), eventManager);
    }

    public void initCommands() {

        commandRepository = new CommandRepository();
        commandDispatcher = new CommandDispatcher(eventManager);
    }

    public void initMacro() {
        macroRepository = new MacroRepository(eventManager);
    }

    public void initDiscordRPC() {
        discordManager = new DiscordManager();
        discordManager.init();
    }

    public void initClientInfoProvider() {
        File clientDirectory = new File(MinecraftClient.getInstance().runDirectory, "\\expensive\\");
        File filesDirectory = new File(clientDirectory, "\\files\\");
        File moduleFilesDirectory = new File(filesDirectory, "\\config\\");
        clientInfoProvider = new ClientInfo(
                "DELTA CLIENT",
                "DELTA",
                "DELTA",
                clientDirectory,
                filesDirectory,
                moduleFilesDirectory);
        System.out.println(clientInfoProvider.getFullInfo());
    }

    public void initFileManager() {
        DirectoryCreator directoryCreator = new DirectoryCreator();
        directoryCreator.createDirectories(clientInfoProvider.clientDir(), clientInfoProvider.filesDir(),
                clientInfoProvider.configsDir());

        fileRepository = new FileRepository();
        fileRepository.setup(this);

        fileController = new FileController(fileRepository.getClientFiles(), clientInfoProvider.filesDir(),
                clientInfoProvider.configsDir());
        try {
            fileController.loadFiles();
        } catch (FileProcessingException e) {
            LoggerUtil.error("Error occurred while loading files: " + e.getMessage() + " " + e.getCause());
        }
    }

    public void initListeners() {
        listenerRepository = new ListenerRepository();
        listenerRepository.setup();
    }

    public void initScissor() {
        scissorManager = new ScissorManager();
    }

    public void toggleLanguage() {
        if (language == Language.ENG) {
            language = Language.RUS;
        } else {
            language = Language.ENG;
        }
    }
}
