package javagems3d.graphics.environment.fog;

import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public abstract class FogScene implements IFogScene {
    private float density;
    private Vector3f color;

    public FogScene() {
        this.density = 0.0f;
        this.color = new Vector3f(0.85f);
    }

    public abstract void updateFogBuffer(ShaderStorageBufferObject shaderStorageBufferObject, ISkyBox skyBox, MemoryStack stack);

    public void setFogColor(Vector3f color) {
        this.color = color;
    }

    public void setFogDensity(float density) {
        this.density = density;
    }

    public void disable() {
        this.setFogDensity(0.0f);
    }

    public Vector3f getFogColor() {
        return new Vector3f(this.color);
    }

    public float getFogDensity() {
        return this.density;
    }
}
