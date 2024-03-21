package ru.BouH.engine.physics.world;

public interface IWorld {
    void onWorldStart();

    void onWorldUpdate();

    void onWorldEnd();

    int getTicks();
    void cleaUp();
}