package ru.expensive.core.client;

import java.io.File;

public interface ClientInfoProvider {
    String clientName();

    String clientVersion();

    String clientBranch();

    String getFullInfo();

    File clientDir();

    File filesDir();

    File configsDir();
}