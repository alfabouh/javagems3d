package ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items;

import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderObjectData;

public class PlayerSPObject extends PhysicsObject {
    public PlayerSPObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    @Override
    public boolean canBeCulled() {
        return false;
    }
}
