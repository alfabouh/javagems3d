package ru.BouH.engine.game.controller;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.render.screen.window.Window;

public interface IController {
    Vector2d getDisplayInput();

    Vector3d getXYZInput();

    void updateControllerState(Window window);
}
