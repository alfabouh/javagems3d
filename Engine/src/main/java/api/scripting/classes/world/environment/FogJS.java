package api.scripting.classes.world.environment;

import api.scripting.classes.util.Vec3f;
import api.scripting.doc.annotations.JSMethodDoc;
import api.scripting.doc.annotations.JSTypeDoc;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.help.JGemsHelper;

@JSTypeDoc(description = "Fog controller object", priority = JSTypeDoc.Priority.MED)
public final class FogJS {
    public FogJS() {
    }

    @JSMethodDoc(description = "Get fog density", args = {}, order = 7)
    public float getFogDensity() {
        return this.getFogScene().getFogDensity();
    }

    @JSMethodDoc(description = "Set fog density", args = {"density"}, order = 6)
    public void setFogDensity(float density) {
        this.getFogScene().setFogDensity(density);
    }

    @JSMethodDoc(description = "Get fog color", args = {}, order = 9)
    public Vec3f getFogColor() {
        return new Vec3f(this.getFogScene().getFogColor());
    }

    @JSMethodDoc(description = "Set fog color", args = {"color"}, order = 8)
    public void setFogColor(Vec3f color) {
        this.getFogScene().setFogColor(color.createJOML());
    }

    private IFogScene getFogScene() {
        return JGemsHelper.get().getSceneWorld().getEnvironment().getFogScene();
    }
}
