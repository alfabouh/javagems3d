package ru.alfabouh.jgems3d.engine.system.core;

public interface IEngine {
    void startSystem();

    EngineSystem.EngineState engineState();
}
