package ru.jgems3d.engine.physics.world.thread.timer;

public interface IPhysTimer {
    void updateTimer(int TPS) throws InterruptedException;
}
