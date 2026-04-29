package javagems3d.graphics.objects.rendering.constructors;

import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ISceneEntityConstructor {
    @NotNull SceneEntity createSceneEntity(SceneWorld sceneWorld, WorldItem worldItem, EntityRenderData entityRenderData);
}
