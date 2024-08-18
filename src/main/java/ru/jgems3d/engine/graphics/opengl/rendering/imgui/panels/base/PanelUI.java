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

package ru.jgems3d.engine.graphics.opengl.rendering.imgui.panels.base;

import org.joml.Vector2i;
import ru.jgems3d.engine.graphics.opengl.rendering.imgui.ImmediateUI;

public interface PanelUI {
    void onConstruct(ImmediateUI immediateUI);

    void onDestruct(ImmediateUI immediateUI);

    void drawPanel(ImmediateUI immediateUI, float frameDeltaTicks);

    void onWindowResize(Vector2i dim);
}
