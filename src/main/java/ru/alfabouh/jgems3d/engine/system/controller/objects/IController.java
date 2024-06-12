package ru.alfabouh.jgems3d.engine.system.controller.objects;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.render.opengl.screen.window.IWindow;
import ru.alfabouh.jgems3d.engine.system.controller.binding.BindingManager;

public interface IController {
    Vector2d getNormalizedRotationInput();
    Vector3d getNormalizedPositionInput();
    BindingManager getBindingManager();
    void updateControllerState(IWindow window);
}
