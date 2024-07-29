package ru.jgems3d.engine.system.controller.dispatcher;

import ru.jgems3d.engine.graphics.opengl.screen.window.IWindow;
import ru.jgems3d.engine.system.controller.objects.IController;

public interface IControllerDispatcher {
    void updateController(IWindow window);
    IController getCurrentController();
}
