package ru.alfabouh.jgems3d.engine.physics.entities.properties.controller;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.system.controller.objects.IController;

public interface IControllable {
    IController currentController();

    void setController(IController iController);

    void performController(Vector2f rotationInput, Vector3f xyzInput, boolean isFocused);

    default boolean isValidController() {
        return this.currentController() != null;
    }

    float getEyeHeight();
}
