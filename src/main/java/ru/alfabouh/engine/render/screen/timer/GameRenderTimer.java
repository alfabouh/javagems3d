package ru.alfabouh.engine.render.screen.timer;

import ru.alfabouh.engine.JGems;

public class GameRenderTimer {
    private boolean shouldBeErased;
    private double lastTime;
    private double deltaTime;
    private double accumulatedTime;

    public GameRenderTimer() {
        this.lastTime = JGems.glfwTime();
        this.shouldBeErased = false;
    }

    public void update() {
        double currentTime = JGems.glfwTime();
        this.deltaTime = currentTime - this.lastTime;
        this.lastTime = currentTime;
        this.accumulatedTime += this.deltaTime;
    }

    public void delete() {
        this.shouldBeErased = true;
    }

    public boolean resetTimerAfterReachedSeconds(double seconds) {
        if (this.accumulatedTime >= seconds) {
            this.accumulatedTime = 0.0d;
            return true;
        }
        return false;
    }

    public boolean isShouldBeErased() {
        return this.shouldBeErased;
    }

    public double getLastTime() {
        return this.lastTime;
    }

    public double getDeltaTime() {
        return this.deltaTime;
    }

    public double getAccumulatedTime() {
        return this.accumulatedTime;
    }
}
