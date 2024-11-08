package ru.expensive.api.system.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;
import ru.expensive.asm.mixins.accessors.ShaderProgramAccessor;
import ru.expensive.common.QuickImports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Shader implements QuickImports, ShaderSetup {
    public static final List<Pair<Function<ResourceFactory, ShaderProgram>, Consumer<ShaderProgram>>> REGISTERED_PROGRAMS = new ArrayList<>();
    public ShaderProgram shaderProgram;

    public Shader(Identifier id, VertexFormat vertexFormat) {
        REGISTERED_PROGRAMS.add(new Pair<>(resourceFactory -> {
            try {
                return new CustomShaderProgram(resourceFactory, id.toString(), vertexFormat);
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialized shader program", e);
            }
        }, program -> {
            shaderProgram = program;
            setup();
        }));
    }

    public void use() {
        RenderSystem.setShader(() -> shaderProgram);
    }

    protected @Nullable GlUniform getUniform(String name) {
        return ((ShaderProgramAccessor) shaderProgram)
                .getUniformsHook()
                .get(name);
    }
}