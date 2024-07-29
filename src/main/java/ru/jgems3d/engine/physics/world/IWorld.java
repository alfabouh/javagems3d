package ru.jgems3d.engine.physics.world;

public interface IWorld {
    void onWorldStart();

    void onWorldUpdate();

    void onWorldEnd();

    int getTicks();
}