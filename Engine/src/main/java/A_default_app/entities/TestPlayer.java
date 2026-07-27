/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package A_default_app.entities;

import javagems3d.JGems3D;
import javagems3d.help.JGemsHelper;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.kinematic.player.JGemsKinematicPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.controller.binding.DefaultBindings;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class TestPlayer extends JGemsKinematicPlayer {
    public TestPlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot);
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
     // if (JGems3D.DEBUG_MODE) {
     //     if (((DefaultBindings) JGemsHelper.controller().getBindingManager()).keyBlock1.isClicked()) {
     //      //   JGemsDynamicBody entityPropInfo = new JGemsDynamicBody(MeshCollider.getDynamic(JGemsResourceManager.globalModelAssets.grassCube), this.getWorld(), this.getPosition().add(this.getLookVector().mul(5.0f)), "A_default_app/horror");
     //      //   JGemsHelper.world().addWorldItem(entityPropInfo, JGemsResourceManager.globalRenderDataAssets.entityCube);
     //      //   Vector3f v3 = this.getLookVector().mul(50.0f);
     //      //  // entityPropInfo.setRotation(new Vector3f((float) Math.toRadians(-90.0f), 0.0f, 0.0f));
     //      //   entityPropInfo.setScaling(new Vector3f(0.5f));
     //      //   entityPropInfo.getPhysicsRigidBody().addLinearVelocity(v3);

     //         JGemsDynamicBody entityPropInfo = new JGemsDynamicBody(MeshCollider.getDynamic(JGemsResourceManager.globalModelAssets.grassCube), this.getWorld(), this.getPosition().add(this.getLookVector().mul(5.0f)), "A_default_app/horror");
     //         JGemsHelper.world().addWorldObjectInBothWorlds(entityPropInfo, JGemsResourceManager.globalRenderDataAssets.entityCube);
     //         Vector3f v3 = this.getLookVector().mul(50.0f);
     //         entityPropInfo.setScaling(new Vector3f(0.95f));
     //         entityPropInfo.getPhysicsRigidBody().addLinearVelocity(v3);
     //     }
     // }
        super.performController(rotationInput, xyzInput, isFocused);
    }
}