package javagems3d.graphics.environment.fog;

import org.joml.Vector3f;

public class FogManager implements IFogManager {
    private float density;
    private Vector3f color;
    public boolean update;

    public FogManager() {
        this.density = -1.0f;
        this.color = new Vector3f(0.85f);
        this.update = true;
    }

    public void setColor(Vector3f color) {
        this.color = color;
        this.update = true;
    }

    public void setDensity(float density) {
        this.density = density;
        this.update = true;
    }

    public void disable() {
        this.setDensity(-1.0f);
    }

    public Vector3f getColor() {
        return new Vector3f(this.color);
    }


    public float getDensity() {
        return this.density;
    }
}
