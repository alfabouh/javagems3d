package javagems3d.physics.entities.properties.controller;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.system.controller.base.IController;

public interface IControllable {
    IController getCurrentController();

    void setController(IController iController);

    void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused);

    default boolean isValidController() {
        return this.getCurrentController() != null;
    }

    float getEyeHeight();
}
