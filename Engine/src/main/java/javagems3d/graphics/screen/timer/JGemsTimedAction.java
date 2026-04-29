package javagems3d.graphics.screen.timer;

import javagems3d.JGems3D;

public final class JGemsTimedAction {
    private boolean shouldBeErased;
    private double lastTime;
    private float deltaTime;
    private double accumulatedTime;

    JGemsTimedAction() {
        this.lastTime = JGems3D.glfwTime();
        this.shouldBeErased = false;
    }

    public void update() {
        double currentTime = JGems3D.glfwTime();
        this.deltaTime = (float) (currentTime - this.lastTime);
        this.lastTime = currentTime;
        this.accumulatedTime += this.deltaTime;
    }

    public void reset() {
        this.lastTime = JGems3D.glfwTime();
        this.accumulatedTime = 0.0f;
    }

    public void dispose() {
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

    public float getDeltaTime() {
        return this.deltaTime;
    }

    public double getAccumulatedTime() {
        return this.accumulatedTime;
    }
}
