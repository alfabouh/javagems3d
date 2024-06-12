package ru.alfabouh.jgems3d.engine.render.opengl.frustum;

public interface ICullable {
    boolean canBeCulled();

    RenderABB getRenderABB();
}
