package api.scripting.classes.world;

import api.scripting.classes.util.Vec3f;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.help.JGemsHelper;
import org.jetbrains.annotations.NotNull;

@JSTypeDoc(description = "Point light in scene world", order = 5)
public class PointLightJS {
    private final PointLight pointLight;

    public PointLightJS(float brightness, @NotNull Vec3f lightPos, @NotNull Vec3f lightColor, @NotNull Vec3f offset) {
        this.pointLight = new PointLight(lightPos.createJOML(), lightColor.createJOML(), offset.createJOML());
        this.pointLight.setBrightness(brightness);
        this.pointLight.on();
    }

    @JSMethodDoc(description = "Turn on", args = {}, order = 0)
    public void enable() {
        this.getPointLight().on();
    }

    @JSMethodDoc(description = "Turn off", args = {}, order = 1)
    public void disable() {
        this.getPointLight().off();
    }

    public void remove() {
        JGemsHelper.world().removeLight(this.getPointLight());
    }

    PointLight getPointLight() {
        return this.pointLight;
    }
}