package javagems3d.graphics.environment.lights.scene;

import javagems3d.physics.world.IWorld;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;
import javagems3d.graphics.world.SceneWorld;

public interface ILightsScene {
    void updateBuffers(MemoryStack stack, IWorld world, Matrix4f viewMatrix);
}
