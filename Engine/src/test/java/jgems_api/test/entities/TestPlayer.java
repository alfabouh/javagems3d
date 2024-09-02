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

package jgems_api.test.entities;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.physics.entities.BtDynamicMeshBody;
import javagems3d.physics.entities.player.SimpleKinematicPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.controller.dispatcher.JGemsControllerDispatcher;
import javagems3d.system.resources.manager.JGemsResourceManager;
import jgems_api.test.manager.bindings.TestBindings;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestPlayer extends SimpleKinematicPlayer {
    public TestPlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot);
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
        if (JGems3D.DEBUG_MODE) {
            if (((TestBindings) JGemsControllerDispatcher.bindingManager()).keyBlock1.isClicked()) {
                BtDynamicMeshBody entityPropInfo = new BtDynamicMeshBody(JGemsResourceManager.globalModelAssets.cube, this.getWorld(), this.getPosition().add(this.getLookVector().mul(5.0f)), "jgems_api/horror");
                JGemsHelper.WORLD.addItemInWorld(entityPropInfo, JGemsResourceManager.globalRenderDataAssets.entityCube);
                Vector3f v3 = this.getLookVector().mul(50.0f);
                entityPropInfo.getPhysicsRigidBody().addLinearVelocity(v3);
            }
        }
        super.performController(rotationInput, xyzInput, isFocused);
    }
}
