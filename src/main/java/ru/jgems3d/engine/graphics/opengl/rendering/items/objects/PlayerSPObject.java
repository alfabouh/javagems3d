package ru.jgems3d.engine.graphics.opengl.rendering.items.objects;

import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;

public class PlayerSPObject extends AbstractSceneEntity {
    public PlayerSPObject(SceneWorld sceneWorld, WorldItem worldItem, RenderEntityData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    @Override
    public boolean canBeCulled() {
        return false;
    }
}
