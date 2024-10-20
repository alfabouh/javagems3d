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

package javagems3d.engine.graphics.opengl.environment.sky;


import org.joml.Vector3f;
import javagems3d.engine.graphics.opengl.environment.sky.skybox.ISkyBox;
import javagems3d.engine.graphics.opengl.rendering.JGemsDebugGlobalConstants;
import javagems3d.engine.physics.world.IWorld;
import javagems3d.engine.physics.world.basic.IWorldTicked;

public class Sky implements IWorldTicked {
    private final ISkyBox skyBox;
    private Vector3f sunPos;
    private Vector3f sunColors;
    private boolean coveredByFog;
    private float sunBrightness;

    public Sky(ISkyBox skyBox, Vector3f sunColors, Vector3f sunPos, float sunBrightness) {
        this.skyBox = skyBox;
        this.coveredByFog = true;
        this.setSunBrightness(sunBrightness);
        this.setSunPos(sunPos);
        this.setSunColors(sunColors);
    }

    public boolean isCoveredByFog() {
        return this.coveredByFog;
    }

    public void setCoveredByFog(boolean coveredByFog) {
        this.coveredByFog = coveredByFog;
    }

    public ISkyBox getSkyBox() {
        return this.skyBox;
    }

    public Vector3f getSunColors() {
        return new Vector3f(this.sunColors);
    }

    public void setSunColors(Vector3f sunColors) {
        this.sunColors = sunColors;
    }

    public Vector3f getSunPos() {
        return new Vector3f(this.sunPos);
    }

    public void setSunPos(Vector3f sunPos) {
        this.sunPos = new Vector3f(sunPos).normalize();
    }

    public float getSunBrightness() {
        return JGemsDebugGlobalConstants.FULL_BRIGHT ? 1.0f : this.sunBrightness;
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
