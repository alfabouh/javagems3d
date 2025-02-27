package javagems3d.graphics.screen.ticking;

public final class FrameTicking {
    private final float physicsSyncTicks;
    private final float frameDeltaTime;

    public FrameTicking(float physicsSyncTicks, float frameDeltaTime) {
        this.physicsSyncTicks = physicsSyncTicks;
        this.frameDeltaTime = frameDeltaTime;
    }

    public float getFrameDeltaTime() {
        return this.frameDeltaTime;
    }

    public float getPhysicsSyncTicks() {
        return this.physicsSyncTicks;
    }
}
