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

package javagems3d.engine.system.controller.dispatcher;

import javagems3d.engine.graphics.opengl.screen.window.IWindow;
import javagems3d.engine.system.controller.objects.IController;

public interface IControllerDispatcher {
    void updateController(IWindow window);

    IController getCurrentController();
}
