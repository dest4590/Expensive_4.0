package ru.expensive.api.system.shape;

import lombok.Builder;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.joml.Vector4i;

@Builder
@Getter
public class ShapeProperties {
    private Matrix4f matrix4f;
    private float x, y, width, height;
    private float softness, thickness;

    private Vector4f round;

    @Builder.Default
    private int outlineColor = -1;
    private Vector4i color;

    @Builder(toBuilder = true)
    private ShapeProperties(Matrix4f matrix4f, float x, float y, float width, float height, float softness,
                            float thickness, Vector4f round, int outlineColor, Vector4i color) {
        this.matrix4f = matrix4f;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.softness = softness;
        this.thickness = thickness;
        this.round = round != null ? round : new Vector4f(0);
        this.outlineColor = outlineColor;
        this.color = color != null ? color : new Vector4i(-1);
    }

    public static ShapeProperties.ShapePropertiesBuilder create(Matrix4f matrix4f, double x, double y, double width,
                                                                double height) {
        return ShapeProperties.builder()
                .matrix4f(matrix4f)
                .x((float) x)
                .y((float) y)
                .width((float) width)
                .height((float) height);
    }

    public static class ShapePropertiesBuilder {

        public ShapePropertiesBuilder color(int color) {
            this.color = new Vector4i(color);
            return this;
        }

        public ShapePropertiesBuilder color(Vector4i color) {
            this.color = color;
            return this;
        }

        public ShapePropertiesBuilder round(float round) {
            this.round = new Vector4f(round);
            return this;
        }

        public ShapePropertiesBuilder round(Vector4f round) {
            this.round = new Vector4f(round);
            return this;
        }

        public ShapePropertiesBuilder color(int... color) {
            this.color = new Vector4i(color);
            return this;
        }

        public ShapePropertiesBuilder round(float... round) {
            this.round = new Vector4f(round);
            return this;
        }
    }
}