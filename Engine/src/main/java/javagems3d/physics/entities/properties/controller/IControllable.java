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

package javagems3d.physics.entities.properties.controller;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.system.controller.objects.IController;

public interface IControllable {
    IController getCurrentController();

    void setController(IController iController);

    void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused);

    default boolean isValidController() {
        return this.getCurrentController() != null;
    }

    float getEyeHeight();
}
