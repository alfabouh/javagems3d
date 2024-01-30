package ru.BouH.engine.render.screen.timer;

import ru.BouH.engine.game.Game;

public class Timer {
    private double lastTime = -1L;

    public double getDeltaTime() {
        double currentTime = Game.glfwTime();
        double deltaTime = (currentTime - this.lastTime);
        this.lastTime = currentTime;
        if (this.lastTime < 0) {
            return 0.0d;
        }
        return deltaTime;
    }
}
