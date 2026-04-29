package javagems3d.graphics.objects.entities.world;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.world.SceneWorld;

import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public class SceneWorldProp extends SceneProp {
    public SceneWorldProp(@NotNull String name, @NotNull SceneWorld sceneWorld, @NotNull PropRenderData propRenderData) {
        super(name, sceneWorld, propRenderData);
    }
}
