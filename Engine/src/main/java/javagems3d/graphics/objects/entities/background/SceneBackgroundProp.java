package javagems3d.graphics.objects.entities.background;

import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;

import javagems3d.system.resources.assets.models.Model3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class SceneBackgroundProp extends SceneProp {
    public SceneBackgroundProp(@NotNull SceneWorld sceneWorld, @NotNull PropRenderData propRenderData) {
        super(sceneWorld, propRenderData);
    }

    @Override
    public final @NotNull Set<ILightAttached> getAttachedLights() {
        return Collections.emptySet();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
    }
}
