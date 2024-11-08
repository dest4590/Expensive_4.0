package ru.expensive.api.system.shader;

import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;

import java.io.IOException;

public class CustomShaderProgram extends ShaderProgram {
    public CustomShaderProgram(ResourceFactory factory, String name, VertexFormat format) throws IOException {
        super(factory, name, format);
    }
}
