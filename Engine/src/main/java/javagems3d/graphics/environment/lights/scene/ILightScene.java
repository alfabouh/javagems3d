package javagems3d.graphics.environment.lights.scene;

import javagems3d.graphics.environment.lights.Light;
import javagems3d.physics.world.IWorld;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

public interface ILightScene {
    void updateBuffers(MemoryStack stack, IWorld world, Matrix4f viewMatrix);
    void addLight(Light light);
    void removeLight(Light light);
}
