package ru.alfabouh.engine.render.frustum;

public interface ICullable {
    boolean canBeCulled();

    RenderABB getRenderABB();
}
