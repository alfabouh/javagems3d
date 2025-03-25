package javagems3d.system.resources.assets.texturing.colors;

import javagems3d.system.resources.assets.texturing.ISample;
import org.joml.Vector2f;

public final class Color2Texture implements ISampleColor2 {
    private final Vector2f color;

    public Color2Texture(float r, float g) {
        this.color = new Vector2f(r, g);
    }

    public Color2Texture(Vector2f color) {
        this.color = color;
    }

    public void setColor(Vector2f color) {
        this.color.set(color);
    }

    @Override
    public Vector2f getColor() {
        return new Vector2f(this.color);
    }
}