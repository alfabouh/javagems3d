package ru.BouH.engine.render.frustum;

public interface ICullable {
    boolean canBeCulled();

    RenderABB getRenderABB();
}
