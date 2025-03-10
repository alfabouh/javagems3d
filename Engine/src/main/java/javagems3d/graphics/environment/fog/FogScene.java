package javagems3d.graphics.environment.fog;

import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public abstract class FogScene implements IFogScene {
    private float density;
    private Vector3f color;
    public boolean update;

    public FogScene() {
        this.density = 0.0f;
        this.color = new Vector3f(0.85f);
        this.update = true;
    }

    public abstract void updateFogBuffer(ShaderStorageBufferObject shaderStorageBufferObject, ISkyBox skyBox, MemoryStack stack);

    public void setColor(Vector3f color) {
        this.color = color;
        this.update = true;
    }

    public void setDensity(float density) {
        this.density = density;
        this.update = true;
    }

    public void disable() {
        this.setDensity(0.0f);
    }

    public Vector3f getColor() {
        return new Vector3f(this.color);
    }

    public float getDensity() {
        return this.density;
    }
}
