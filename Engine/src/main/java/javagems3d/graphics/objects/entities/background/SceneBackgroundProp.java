package javagems3d.graphics.objects.entities.background;

import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class SceneBackgroundProp extends SceneProp {
    public SceneBackgroundProp(@NotNull SceneWorld sceneWorld, @Nullable Model<Format3D> model, @NotNull RenderAttributes objectRenderingConfiguration) {
        super(sceneWorld, model, objectRenderingConfiguration);
    }

    public void clearLights() {
        Iterator<Light> lightIterator = this.getLightsList().iterator();
        while (lightIterator.hasNext()) {
            Light l = lightIterator.next();
            l.off();
            this.onRemoveLight(l);
            lightIterator.remove();
        }
    }

    public void addLight(Light light) {
        this.getLightsList().add(light);
        light.on();
        this.onAddLight(light);
    }

    public void removeLight(Light light) {
        this.getLightsList().remove(light);
        light.off();
        this.onRemoveLight(light);
    }

    protected void onAddLight(Light light) {
        JGemsHelper.getLogger().log("Add light to: " + this);
    }

    protected void onRemoveLight(Light light) {
        JGemsHelper.getLogger().log("Removed light from: " + this);
    }
}
