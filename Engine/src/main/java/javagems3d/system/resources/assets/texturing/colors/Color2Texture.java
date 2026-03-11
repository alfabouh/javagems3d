package javagems3d.system.resources.assets.texturing.colors;

import org.joml.Vector2f;

public record Color2Texture(Vector2f color) implements ISampleColor2 {
    public Color2Texture(float r, float g) {
        this(new Vector2f(r, g));
    }

    public void setColor(Vector2f color) {
        this.color.set(color);
    }

    @Override
    public Vector2f color() {
        return new Vector2f(this.color);
    }
}