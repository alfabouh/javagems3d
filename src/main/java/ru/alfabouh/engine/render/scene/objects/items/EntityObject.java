package ru.alfabouh.engine.render.scene.objects.items;

import ru.alfabouh.engine.physics.entities.PhysEntity;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.engine.render.scene.world.SceneWorld;

public class EntityObject extends PhysicsObject {
    private final PhysEntity physEntity;

    public EntityObject(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        this(sceneWorld, (PhysEntity) worldItem, renderData);
    }

    private EntityObject(SceneWorld sceneWorld, PhysEntity physEntity, RenderObjectData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    public PhysEntity getEntity() {
        return this.physEntity;
    }
}
