package javagems3d.graphics.objects.entities.background;

import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SceneBackgroundProp extends SceneProp {
    public SceneBackgroundProp(@NotNull SceneWorld sceneWorld, @Nullable Model<Format3D> model, @NotNull RenderAttributes objectRenderingConfiguration) {
        super(sceneWorld, model, objectRenderingConfiguration);
    }

    public final void clearLights() {
    }

    public final void addLight(Light light) {
    }

    public final void removeLight(Light light) {
    }

    protected final void onAddLight(Light light) {
    }

    protected final void onRemoveLight(Light light) {
    }

    @Override
    public final List<Light> getLightsList() {
        return Collections.emptyList();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
