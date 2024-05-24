package ru.alfabouh.engine.game;

public interface IEngine {
    void startSystem();
    GameSystem.EngineState engineState();
}
