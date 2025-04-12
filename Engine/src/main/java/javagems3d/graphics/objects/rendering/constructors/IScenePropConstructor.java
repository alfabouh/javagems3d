package javagems3d.graphics.objects.rendering.constructors;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.world.SceneWorld;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IScenePropConstructor {
    @NotNull SceneProp createSceneProp(String name, SceneWorld sceneWorld, PropRenderData propRenderData);
}
