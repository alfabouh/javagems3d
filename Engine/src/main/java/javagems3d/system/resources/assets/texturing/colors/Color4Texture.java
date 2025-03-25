package javagems3d.system.resources.assets.texturing.colors;

import javagems3d.system.resources.assets.texturing.ISample;
import org.joml.Vector4f;

public final class Color4Texture implements ISampleColor4 {
    private final Vector4f color;

    public Color4Texture(float r, float g, float b) {
        this(r, g, b, 1.0f);
    }

    public Color4Texture(float r, float g, float b, float a) {
        this(new Vector4f(r, g, b, a));
    }

    public Color4Texture(Vector4f color) {
        this.color = color;
    }

    public void setColor(Vector4f color) {
        this.color.set(color);
    }

    @Override
    public Vector4f getColor() {
        return new Vector4f(this.color);
    }
}