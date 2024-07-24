package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects;

import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.world.SceneWorld;

public class PlayerSPObject extends AbstractSceneEntity {
    public PlayerSPObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    @Override
    public boolean canBeCulled() {
        return false;
    }
}
