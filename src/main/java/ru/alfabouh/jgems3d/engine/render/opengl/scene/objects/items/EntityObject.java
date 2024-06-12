package ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.items;

import ru.alfabouh.jgems3d.engine.physics.entities.PhysEntity;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.world.SceneWorld;

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
