package javagems3d.system.controller.dispatcher;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.system.controller.base.IController;

public interface IControllerDispatcher {
    void updateController(IWindow window);

    IController getCurrentController();
}
