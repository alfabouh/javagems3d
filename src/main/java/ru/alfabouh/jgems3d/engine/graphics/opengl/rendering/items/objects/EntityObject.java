package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.objects;

import ru.alfabouh.jgems3d.engine.physics.entities.BtBody;
import ru.alfabouh.jgems3d.engine.physics.world.basic.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.world.SceneWorld;

public class EntityObject extends AbstractSceneEntity {
    private final BtBody physEntity;

    public EntityObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        this(sceneWorld, (BtBody) worldItem, renderData);
    }

    private EntityObject(SceneWorld sceneWorld, BtBody physEntity, RenderObjectData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    public BtBody getEntity() {
        return this.physEntity;
    }
}
