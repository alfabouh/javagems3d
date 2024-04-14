package ru.alfabouh.engine.render.scene.objects.items;

import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.engine.render.scene.world.SceneWorld;

public class PlayerSPObject extends PhysicsObject {
    public PlayerSPObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    @Override
    public boolean canBeCulled() {
        return false;
    }
}
