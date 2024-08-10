package jgems_api.example.entities;

import jgems_api.example.manager.bindings.TestBindings;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.physics.entities.BtDynamicMeshBody;
import ru.jgems3d.engine.physics.entities.player.SimpleKinematicPlayer;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.system.controller.dispatcher.JGemsControllerDispatcher;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class TestPlayer extends SimpleKinematicPlayer {
    public TestPlayer(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, pos, rot);
    }

    @Override
    public void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused) {
        if (JGems3D.DEBUG_MODE) {
            if (((TestBindings) JGemsControllerDispatcher.bindingManager()).keyBlock1.isPressed()) {
                BtDynamicMeshBody entityPropInfo = new BtDynamicMeshBody(JGemsResourceManager.globalModelAssets.cube, this.getWorld(), this.getPosition().add(this.getLookVector().mul(5.0f)), "test");
                JGemsHelper.addItem(entityPropInfo, JGemsResourceManager.globalRenderDataAssets.entityCube);
                Vector3f v3 = this.getLookVector().mul(50.0f);
                entityPropInfo.getPhysicsRigidBody().addLinearVelocity(v3);
            }
        }
        super.performController(rotationInput, xyzInput, isFocused);
    }
}
