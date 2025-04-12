package jgems_app.entities;

import javagems3d.JGems3D;
import javagems3d.help.JGemsControllerHelper;
import javagems3d.help.JGemsWorldHelper;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.bodies.JGemsDynamicBody;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
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
        if (JGems3D.DEBUG_MODE) {
            if (((DefaultBindings) JGemsControllerHelper.bindingManager()).keyBlock1.isClicked()) {
                JGemsDynamicBody entityPropInfo = new JGemsDynamicBody(MeshCollider.getDynamic(JGemsResourceManager.globalModelAssets.grassCube), this.getWorld(), this.getPosition().add(this.getLookVector().mul(5.0f)), "jgems_app/horror");
                JGemsWorldHelper.addItemInWorld(entityPropInfo, JGemsResourceManager.globalRenderDataAssets.entityCube);
                Vector3f v3 = this.getLookVector().mul(50.0f);
               // entityPropInfo.setRotation(new Vector3f((float) Math.toRadians(-90.0f), 0.0f, 0.0f));
                entityPropInfo.setScaling(new Vector3f(0.05f));
                entityPropInfo.getPhysicsRigidBody().addLinearVelocity(v3);
            }
        }
        super.performController(rotationInput, xyzInput, isFocused);
    }
}