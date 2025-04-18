package api.scripting.classes.world;

import api.scripting.classes.util.Vec3f;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.help.JGemsHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@JSTypeDoc(description = "Point light in scene world", priority = JSTypeDoc.Priority.MED)
public final class PointLightJS extends ObjectJS {
    private final PointLight pointLight;

    public PointLightJS(PointLight pointLight) {
        this.pointLight = pointLight;
    }

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

    @JSMethodDoc(description = "Get light brightness", args = {}, order = 2)
    public float getLightBrightness() {
        return this.getPointLight().getBrightness();
    }

    @JSMethodDoc(description = "Set light brightness", args = {"brightness"}, order = 3)
    public void setLightBrightness(float brightness) {
        this.getPointLight().setBrightness(brightness);
    }

    @JSMethodDoc(description = "Get light position", args = {}, order = 4)
    public Vec3f getLightPosition() {
        Vector3f position = this.getPointLight().getLightPosition();
        return new Vec3f(position.x, position.y, position.z);
    }

    @JSMethodDoc(description = "Set light position", args = {"position"}, order = 5)
    public void setLightPosition(Vec3f position) {
        this.getPointLight().setLightPosition(position.createJOML());
    }

    @JSMethodDoc(description = "Get light offset", args = {}, order = 6)
    public Vec3f getLightOffset() {
        Vector3f offset = this.getPointLight().getOffset();
        return new Vec3f(offset.x, offset.y, offset.z);
    }

    @JSMethodDoc(description = "Set light offset", args = {"offset"}, order = 7)
    public void setLightOffset(Vec3f offset) {
        this.getPointLight().setOffset(offset.createJOML());
    }

    @JSMethodDoc(description = "Get light color", args = {}, order = 8)
    public Vec3f getLightColor() {
        Vector3f color = this.getPointLight().getLightColor();
        return new Vec3f(color.x, color.y, color.z);
    }

    @JSMethodDoc(description = "Set light color", args = {"color"}, order = 9)
    public void setLightColor(Vec3f color) {
        this.getPointLight().setLightColor(color.createJOML());
    }

    public void remove() {
        JGemsHelper.world().removeLight(this.getPointLight());
    }

    public PointLight getPointLight() {
        return this.pointLight;
    }
}