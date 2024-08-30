/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.graphics.opengl.rendering.scene.tick;

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
