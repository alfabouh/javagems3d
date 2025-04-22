package api.scripting.classes.world.environment;

import api.scripting.classes.util.Vec3f;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.help.JGemsHelper;

@JSTypeDoc(description = "Sky controller object", priority = JSTypeDoc.Priority.MED)
public final class SkyJS {
    public SkyJS() {
    }

    @JSMethodDoc(description = "Get sun brightness", args = {}, order = 3)
    public float getSunBrightness() {
        return this.getSkyBox().getSun().getSunBrightness();
    }

    @JSMethodDoc(description = "Set sun brightness", args = {"brightness"}, order = 0)
    public void setSunBrightness(float brightness) {
        this.getSkyBox().getSun().setSunBrightness(brightness);
    }

    @JSMethodDoc(description = "Get sun position", args = {}, order = 4)
    public Vec3f getSunPosition() {
        return new Vec3f(this.getSkyBox().getSun().getLightPosition());
    }

    @JSMethodDoc(description = "Set sun position", args = {"position"}, order = 1)
    public void setSunPosition(Vec3f position) {
        this.getSkyBox().getSun().setLightPosition(position.createJOML());
    }

    @JSMethodDoc(description = "Get sun color", args = {}, order = 5)
    public Vec3f getColor() {
        return new Vec3f(this.getSkyBox().getSun().getLightColor());
    }

    @JSMethodDoc(description = "Set sun color", args = {"color"}, order = 2)
    public void setColor(Vec3f color) {
        this.getSkyBox().getSun().setLightColor(color.createJOML());
    }

    private ISkyBox getSkyBox() {
        return JGemsHelper.get().getSceneWorld().getEnvironment().getSkyBox();
    }
}
