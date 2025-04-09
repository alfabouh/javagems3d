package javagems3d.graphics.environment.fog;

import org.joml.Vector3f;

public interface IFogScene {
    void setColor(Vector3f color);
    void setDensity(float density);

    Vector3f getColor();
    float getDensity();
}
