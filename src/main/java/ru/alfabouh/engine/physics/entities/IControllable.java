package ru.alfabouh.engine.physics.entities;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.alfabouh.engine.game.controller.input.IController;

public interface IControllable {
    IController currentController();

    void setController(IController iController);

    void performController(Vector2d rotationInput, Vector3d xyzInput, boolean isFocused);

    default boolean isValidController() {
        return this.currentController() != null;
    }

    double getEyeHeight();
}
