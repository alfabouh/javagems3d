package javagems3d.graphics.objects.entities.world;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.world.SceneWorld;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SceneWorldProp extends SceneProp {
    public SceneWorldProp(@NotNull SceneWorld sceneWorld, @Nullable Model3D model, @NotNull RenderAttributes objectRenderingConfiguration) {
        super(sceneWorld, model, objectRenderingConfiguration);
    }
}
