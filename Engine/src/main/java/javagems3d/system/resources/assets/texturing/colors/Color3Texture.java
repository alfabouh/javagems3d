package javagems3d.system.resources.assets.texturing.colors;

import javagems3d.system.resources.assets.texturing.ISample;
import org.joml.Vector3f;

public final class Color3Texture implements ISampleColor3 {
    private final Vector3f color;

    public Color3Texture(float r, float g, float b) {
        this.color = new Vector3f(r, g, b);
    }

    public Color3Texture(Vector3f color) {
        this.color = color;
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }

    @Override
    public Vector3f getColor() {
        return new Vector3f(this.color);
    }
}