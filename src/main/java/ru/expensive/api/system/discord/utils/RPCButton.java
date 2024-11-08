package ru.expensive.api.system.discord.utils;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class RPCButton implements Serializable {
    private final String url;
    private final String label;

    protected RPCButton(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public static RPCButton create(String label, String url) {
        label = label.substring(0, Math.min(label.length(), 31));
        return new RPCButton(label, url);
    }
}