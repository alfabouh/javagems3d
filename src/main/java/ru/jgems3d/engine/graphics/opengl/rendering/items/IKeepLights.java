package ru.jgems3d.engine.graphics.opengl.rendering.items;

import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.environment.light.Light;

import java.util.List;

public interface IKeepLights {
    void addLight(Light light);

    void removeLight(Light light);

    List<Light> getLightList();

    default void adjustLightsTranslation(Vector3f pos, Vector3f offset) {
        for (Light l : this.getLightList()) {
            l.setLightPos(pos);
            l.setOffset(offset);
        }
    }

    default boolean removeLightById(int id) {
        if (this.getLightList().size() <= id) {
            this.removeLight(this.getLightList().get(id));
            return true;
        }
        return false;
    }

    default boolean hasLights() {
        return !this.getLightList().isEmpty();
    }
}
