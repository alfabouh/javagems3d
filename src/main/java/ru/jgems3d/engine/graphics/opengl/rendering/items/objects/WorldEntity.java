package ru.jgems3d.engine.graphics.opengl.rendering.items.objects;

import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;

public class WorldEntity extends AbstractSceneEntity {
    public WorldEntity(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }
}
