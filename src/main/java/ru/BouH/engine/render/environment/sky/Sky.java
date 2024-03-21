package ru.BouH.engine.render.environment.sky;


import org.joml.Vector3f;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.render.environment.sky.skybox.ISkyBox;

public class Sky implements IWorldDynamic {
    private final ISkyBox skyBox;
    private Vector3f sunAngle;
    private Vector3f sunColors;
    private float sunBrightness;

    public Sky(ISkyBox skyBox, Vector3f sunColors, Vector3f sunAngle, float sunBrightness) {
        this.skyBox = skyBox;
        this.setSunBrightness(sunBrightness);
        this.setSunAngle(sunAngle);
        this.setSunColors(sunColors);
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
        return this.sunBrightness;
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
