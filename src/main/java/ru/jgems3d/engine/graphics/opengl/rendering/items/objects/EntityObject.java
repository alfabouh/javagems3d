/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.graphics.opengl.rendering.items.objects;

import ru.jgems3d.engine.physics.entities.BtBody;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;

public class EntityObject extends AbstractSceneEntity {
    private final BtBody physEntity;

    public EntityObject(SceneWorld sceneWorld, WorldItem worldItem, RenderEntityData renderData) {
        this(sceneWorld, (BtBody) worldItem, renderData);
    }

    private EntityObject(SceneWorld sceneWorld, BtBody physEntity, RenderEntityData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    public BtBody getEntity() {
        return this.physEntity;
    }
}
