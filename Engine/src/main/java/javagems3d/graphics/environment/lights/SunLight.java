package javagems3d.graphics.environment.lights;

import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class SunLight extends Light {
    private float sunBrightness;

    public SunLight(@NotNull Vector3f sunPos, @NotNull Vector3f sunColor, float sunBrightness) {
        super(sunPos, sunColor);
        this.sunBrightness = sunBrightness;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }

    public void setSunBrightness(float sunBrightness) {
        this.sunBrightness = sunBrightness;
    }

    public float getSunBrightness() {
        return this.sunBrightness;
    }

    @Override
    public LightType getLightType() {
        return LightType.SUN;
    }
}
