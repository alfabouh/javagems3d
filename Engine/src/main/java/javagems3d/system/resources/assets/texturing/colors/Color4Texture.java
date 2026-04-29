package javagems3d.system.resources.assets.texturing.colors;

import org.joml.Vector4f;

public record Color4Texture(Vector4f color) implements ISampleColor4 {
    public Color4Texture(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color4Texture(float r, float g, float b, float a) {
        this(new Vector4f(r, g, b, a));
    }

    public void setColor(Vector4f color) {
        this.color.set(color);
    }

    @Override
    public Vector4f color() {
        return new Vector4f(this.color);
    }
}