package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class WorldItemObject extends PhysicsObject {
    public WorldItemObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }
}
