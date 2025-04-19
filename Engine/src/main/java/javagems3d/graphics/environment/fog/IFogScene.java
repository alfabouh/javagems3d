package javagems3d.graphics.environment.fog;

import org.joml.Vector3f;

public interface IFogScene {
    void setFogColor(Vector3f color);
    void setFogDensity(float density);

    Vector3f getFogColor();
    float getFogDensity();
}
