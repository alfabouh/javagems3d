package ru.BouH.engine.proxy;

public interface IWorld {
    void onWorldStart();

    void onWorldUpdate();

    void onWorldEnd();

    int getTicks();
}