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
import javagems3d.physics.world.basic.WorldItem;

public class PlayerSPObject extends AbstractSceneEntity {
    public PlayerSPObject(SceneWorld sceneWorld, WorldItem worldItem, RenderEntityData renderData) {
        super(sceneWorld, worldItem, renderData);
    }

    @Override
    public boolean canBeCulled() {
        return false;
    }
}
