package ru.alfabouh.jgems3d.engine.render.opengl.environment.sky;


import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.sky.skybox.ISkyBox;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.debug.constants.GlobalRenderDebugConstants;

public class Sky implements IWorldDynamic {
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
        return GlobalRenderDebugConstants.FULL_BRIGHT ? 1.0f : this.sunBrightness;
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
