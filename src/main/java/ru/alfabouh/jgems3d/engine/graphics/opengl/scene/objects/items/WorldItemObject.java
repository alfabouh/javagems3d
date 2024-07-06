package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items;

import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.SceneWorld;

public class WorldItemObject extends AbstractSceneItemObject {
    public WorldItemObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }
}
