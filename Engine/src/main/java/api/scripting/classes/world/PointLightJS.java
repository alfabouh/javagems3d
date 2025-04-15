package api.scripting.classes.world;

import api.scripting.classes.util.Vec3f;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.help.JGemsWorldHelper;
import org.jetbrains.annotations.NotNull;

public class PointLightJS {
    private final PointLight pointLight;

    public PointLightJS(float brightness, @NotNull Vec3f lightPos, @NotNull Vec3f lightColor, @NotNull Vec3f offset) {
        this.pointLight = new PointLight(lightPos.createJOML(), lightColor.createJOML(), offset.createJOML());
        this.pointLight.setBrightness(brightness);
        this.pointLight.on();
    }

    public void enable() {
        this.getPointLight().on();
    }

    public void disable() {
        this.getPointLight().off();
    }

    public void remove() {
        JGemsWorldHelper.removeLight(this.getPointLight());
    }

    PointLight getPointLight() {
        return this.pointLight;
    }
}