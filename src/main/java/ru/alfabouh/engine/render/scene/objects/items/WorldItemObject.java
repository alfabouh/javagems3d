package ru.alfabouh.engine.render.scene.objects.items;

import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.engine.render.scene.world.SceneWorld;

public class WorldItemObject extends PhysicsObject {
    public WorldItemObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }
}
