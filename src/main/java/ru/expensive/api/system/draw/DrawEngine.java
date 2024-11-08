package ru.expensive.api.system.draw;

import org.joml.Matrix4f;

public interface DrawEngine {

    void quad(Matrix4f matrix4f, float x, float y, float width, float height);

    void quad(Matrix4f matrix4f, float x, float y, float width, float height, int color);
}
