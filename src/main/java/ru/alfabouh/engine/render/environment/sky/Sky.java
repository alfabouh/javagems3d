package ru.alfabouh.engine.render.environment.sky;


import org.joml.Vector3f;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.engine.render.environment.sky.skybox.ISkyBox;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.debug.constants.GlobalRenderDebugConstants;

public class Sky implements IWorldDynamic {
    private final ISkyBox skyBox;
    private Vector3f sunAngle;
    private Vector3f sunColors;
    private boolean coveredByFog;
    private float sunBrightness;

    public Sky(ISkyBox skyBox, Vector3f sunColors, Vector3f sunAngle, float sunBrightness) {
        this.skyBox = skyBox;
        this.coveredByFog = true;
        this.setSunBrightness(sunBrightness);
        this.setSunAngle(sunAngle);
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

    public Vector3f getSunAngle() {
        return new Vector3f(this.sunAngle);
    }

    public void setSunAngle(Vector3f sunAngle) {
        this.sunAngle = new Vector3f(sunAngle).normalize();
    }

    public float getSunBrightness() {
        return GlobalRenderDebugConstants.FULL_BRIGHT ? 1.0f : this.sunBrightness;
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
