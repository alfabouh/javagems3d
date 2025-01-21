package javagems3d.graphics.objects.entities.world;

import javagems3d.graphics.objects.entities.SceneEntity;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.basic.WorldItem;
import org.jetbrains.annotations.NotNull;

public class SceneWorldEntity extends SceneEntity {
    public SceneWorldEntity(@NotNull SceneWorld sceneWorld, @NotNull WorldItem worldItem, @NotNull EntityRenderData renderData) {
        super(sceneWorld, worldItem, renderData);
    }
}
