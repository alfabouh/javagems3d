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

package javagems3d.graphics.opengl.rendering.items.objects;

import javagems3d.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.entities.bullet.JGemsBody;
import javagems3d.physics.world.basic.WorldItem;

public class EntityObject extends AbstractSceneEntity {
    private final JGemsBody physEntity;

    public EntityObject(SceneWorld sceneWorld, WorldItem worldItem, RenderEntityData renderData) {
        this(sceneWorld, (JGemsBody) worldItem, renderData);
    }

    private EntityObject(SceneWorld sceneWorld, JGemsBody physEntity, RenderEntityData renderData) {
        super(sceneWorld, physEntity, renderData);
        this.physEntity = physEntity;
    }

    public JGemsBody getEntity() {
        return this.physEntity;
    }
}
