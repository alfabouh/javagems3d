package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.world.SceneWorld;

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
