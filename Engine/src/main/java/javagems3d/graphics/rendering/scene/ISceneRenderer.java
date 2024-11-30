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

package javagems3d.graphics.rendering.scene;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.window.Window;
import javagems3d.graphics.screen.ticking.FrameTicking;

public interface ISceneRenderer extends IWindow.ResizeCallback {
    void onStartRender();

    void onRender(FrameTicking frameTicking);

    void onStopRender();

    IWindow getWindow();

    JGemsSceneData getSceneData();
}
