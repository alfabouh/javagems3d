package javagems3d.system.resources.assets.texturing.colors;

import org.joml.Vector3f;

public record Color3Texture(Vector3f color) implements ISampleColor3 {
    public Color3Texture(float r, float g, float b) {
        this(new Vector3f(r, g, b));
    }

    public void setColor(Vector3f color) {
        this.color.set(color);
    }

    @Override
    public Vector3f color() {
        return new Vector3f(this.color);
    }
}