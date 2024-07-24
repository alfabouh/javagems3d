package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects;

import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.world.SceneWorld;

public class WorldEntity extends AbstractSceneEntity {
    public WorldEntity(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }
}
