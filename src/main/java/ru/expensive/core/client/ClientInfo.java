package ru.expensive.core.client;

import java.io.File;

public record ClientInfo(String clientName, String clientVersion, String clientBranch, File clientDir, File filesDir,
                         File configsDir)
        implements ClientInfoProvider {

    @Override
    public String getFullInfo() {
        return String.format("Welcome! Client: %s Version: %s Branch: %s", clientName, clientVersion, clientBranch);
    }
}