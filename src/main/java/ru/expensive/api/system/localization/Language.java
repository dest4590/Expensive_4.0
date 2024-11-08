package ru.expensive.api.system.localization;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;

@Getter
@RequiredArgsConstructor
public enum Language {
    ENG("en_US"),
    RUS("ru_RU");

    private final String file;
    private final HashMap<String, String> strings = new HashMap<>();
}