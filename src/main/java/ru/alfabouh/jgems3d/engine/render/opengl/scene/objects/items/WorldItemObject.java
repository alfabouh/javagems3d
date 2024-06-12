package ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items;

import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderObjectData;

public class WorldItemObject extends PhysicsObject {
    public WorldItemObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }
}
