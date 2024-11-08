package ru.expensive.api.file;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.api.file.exception.FileLoadException;
import ru.expensive.api.file.exception.FileSaveException;
import ru.expensive.api.file.impl.module.ModuleFile;
import ru.expensive.common.util.logger.LoggerUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    List<ClientFile> clientFiles;
    File directory, moduleConfigDirectory;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public FileController(List<ClientFile> clientFiles, File directory, File moduleConfigDirectory) {
        this.clientFiles = clientFiles;
        this.directory = directory;
        this.moduleConfigDirectory = moduleConfigDirectory;
        startAutoSave();
    }

    private void startAutoSave() {
        LoggerUtil.info("Auto-save system started!");
        scheduler.scheduleAtFixedRate(() -> {
            try {
                LoggerUtil.info("Saving with auto-save.");
                saveFiles();
            } catch (FileSaveException e) {
                LoggerUtil.error("Failed to auto-save files: " + e.getMessage());
            }
        }, 2, 2, TimeUnit.MINUTES);
    }

    public void stopAutoSave() {
        LoggerUtil.info("Auto-save shutdown!");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    public void saveFiles() throws FileSaveException {
        if (clientFiles.isEmpty()) {
            LoggerUtil.warn("No files to save from directory: " + directory.getPath());
            return;
        }

        for (ClientFile clientFile : clientFiles) {
            try {
                clientFile.saveToFile(directory);
                LoggerUtil.info("Successfully saved file: " + clientFile.getName() + " to " + directory.getPath());
            } catch (FileSaveException e) {
                throw new FileSaveException("Failed to save file: " + clientFile.getName(), e);
            }
        }
    }

    public void loadFiles() throws FileLoadException {
        if (clientFiles.isEmpty()) {
            LoggerUtil.warn("No files to load from directory: " + directory.getPath());
            return;
        }

        for (ClientFile clientFile : clientFiles) {
            try {
                clientFile.loadFromFile(directory);
                LoggerUtil.info("Successfully loaded file: " + clientFile.getName() + " from " + directory.getPath());
            } catch (FileLoadException e) {
                throw new FileLoadException("Failed to load file: " + clientFile.getName(), e);
            }
        }
    }

    public void saveFile(String fileName) throws FileSaveException {
        for (ClientFile clientFile : clientFiles) {
            if (clientFile instanceof ModuleFile) {
                try {
                    clientFile.saveToFile(moduleConfigDirectory, fileName);
                    LoggerUtil.info("Successfully saved file: " + fileName + " to " + moduleConfigDirectory.getPath());
                } catch (FileSaveException e) {
                    throw new FileSaveException("Failed to save file: " + fileName, e);
                }
            }
        }
    }

    public void loadFile(String fileName) throws FileLoadException {
        for (ClientFile clientFile : clientFiles) {
            if (clientFile instanceof ModuleFile) {
                try {
                    clientFile.loadFromFile(moduleConfigDirectory, fileName);
                    LoggerUtil
                            .info("Successfully loaded file: " + fileName + " from " + moduleConfigDirectory.getPath());
                } catch (FileLoadException e) {
                    throw new FileLoadException("Failed to load file: " + fileName, e);
                }
            }
        }
    }
}
