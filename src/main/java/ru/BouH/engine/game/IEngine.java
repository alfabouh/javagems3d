package ru.BouH.engine.game;

public interface IEngine {
    void startSystem();

    GameSystem.EngineState engineState();
}
