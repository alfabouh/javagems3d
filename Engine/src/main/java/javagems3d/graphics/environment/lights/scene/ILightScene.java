package javagems3d.graphics.environment.lights.scene;

import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.physics.world.IWorld;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Set;

public interface ILightScene extends IHasHDR, IHasSSAO {
    void updateBuffers(MemoryStack stack, HashMap<PointLight, Integer> lightIntegerHashMap, IWorld world, Matrix4f viewMatrix);
    void addLight(Light light);
    void removeLight(Light light);

    Set<PointLight> getPointLights();
}
