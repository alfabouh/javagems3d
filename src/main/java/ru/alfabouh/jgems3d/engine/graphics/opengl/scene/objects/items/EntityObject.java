package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items;

import ru.alfabouh.jgems3d.engine.physics.objects.base.PhysObject;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.world.SceneWorld;

public class EntityObject extends AbstractSceneItemObject {
    private final PhysObject physEntity;

    public EntityObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        this(sceneWorld, (PhysObject) worldItem, renderData);
    }

    private EntityObject(SceneWorld sceneWorld, PhysObject physEntity, RenderObjectData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    public PhysObject getEntity() {
        return this.physEntity;
    }
}
