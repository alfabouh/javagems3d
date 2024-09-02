/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.rendering.items;

import org.joml.Vector3f;
import javagems3d.graphics.opengl.environment.light.Light;

import java.util.List;

public interface ILightsKeeper {
    void addLight(Light light);

    void removeLight(Light light);

    List<Light> getLightsList();

    default void adjustLightsTranslation(Vector3f pos, Vector3f offset) {
        for (Light l : this.getLightsList()) {
            l.setLightPos(pos);
            l.setOffset(offset);
        }
    }

    @SuppressWarnings("all")
    default boolean removeLightById(int id) {
        if (this.getLightsList().size() <= id) {
            this.removeLight(this.getLightsList().get(id));
            return true;
        }
        return false;
    }

    default boolean hasLights() {
        return !this.getLightsList().isEmpty();
    }
}
