package ru.expensive.api.system.shader.implement;

import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import ru.expensive.api.system.shader.Shader;

public class RoundShader extends Shader {
    public GlUniform size;
    public GlUniform location;
    public GlUniform radius;

    public GlUniform color1;
    public GlUniform color2;
    public GlUniform color3;
    public GlUniform color4;

    public GlUniform outlineColor;

    public GlUniform softness;
    public GlUniform thickness;

    public RoundShader() {
        super(new Identifier("minecraft", "round"), VertexFormats.POSITION);
    }

    @Override
    public void setup() {
        this.size = this.getUniform("size");
        this.location = this.getUniform("location");
        this.radius = this.getUniform("radius");

        this.color1 = this.getUniform("color1");
        this.color2 = this.getUniform("color2");
        this.color3 = this.getUniform("color3");
        this.color4 = this.getUniform("color4");

        this.outlineColor = this.getUniform("outlineColor");
        this.softness = this.getUniform("softness");
        this.thickness = this.getUniform("thickness");
    }
}
