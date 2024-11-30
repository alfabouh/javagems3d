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

package javagems3d.graphics.rendering.ui.jgems_imgui.panels.base;

import javagems3d.graphics.screen.window.IWindow;
import javagems3d.graphics.screen.window.Window;
import org.joml.Vector2i;
import javagems3d.graphics.rendering.ui.jgems_imgui.ImmediateUI;

public interface PanelUI extends IWindow.ResizeCallback {
    void onConstruct(ImmediateUI immediateUI);

    void onDestruct(ImmediateUI immediateUI);

    void drawPanel(ImmediateUI immediateUI, float frameDeltaTicks);
}
