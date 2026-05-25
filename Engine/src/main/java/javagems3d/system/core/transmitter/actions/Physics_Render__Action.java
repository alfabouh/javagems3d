package javagems3d.system.core.transmitter.actions;

import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;

@FunctionalInterface
public interface Physics_Render__Action {
    void action(SceneWorld sceneWorld);
}
