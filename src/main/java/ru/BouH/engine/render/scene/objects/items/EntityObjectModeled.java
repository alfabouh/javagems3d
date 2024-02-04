package ru.BouH.engine.render.scene.objects.items;

import ru.BouH.engine.physics.entities.PhysEntity;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.render_data.RenderObjectData;
import ru.BouH.engine.render.scene.world.SceneWorld;

public class EntityObjectModeled extends PhysicsObjectModeled {
    private final PhysEntity physEntity;

    public EntityObjectModeled(SceneWorld sceneWorld, WorldItem worldItem, RenderObjectData renderData) {
        this(sceneWorld, (PhysEntity) worldItem, renderData);
    }

    private EntityObjectModeled(SceneWorld sceneWorld, PhysEntity physEntity, RenderObjectData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    public PhysEntity getEntity() {
        return this.physEntity;
    }
}
