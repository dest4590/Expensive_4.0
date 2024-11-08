package ru.expensive.api.system.shader;

import ru.expensive.api.system.shader.implement.RoundShader;

public class ShadersPool {
    public static RoundShader ROUNDED_SHADER;

    public static void initShaders() {
        ROUNDED_SHADER = new RoundShader();
    }
}
