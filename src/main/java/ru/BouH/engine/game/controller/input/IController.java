package ru.BouH.engine.game.controller.input;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.render.screen.window.Window;

public interface IController {
    Vector2d getNormalizedRotationInput();

    Vector3d getNormalizedPositionInput();

    void updateControllerState(Window window);
}
