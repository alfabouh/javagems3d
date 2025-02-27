package javagems3d.system.controller.base;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.binding.BindingManager;

public interface IController {
    Vector2f getNormalizedRotationInput();

    Vector3f getNormalizedPositionInput();

    BindingManager getBindingManager();

    void updateControllerState(IWindow window);
}
