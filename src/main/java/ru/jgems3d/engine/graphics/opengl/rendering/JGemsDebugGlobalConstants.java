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

package ru.jgems3d.engine.graphics.opengl.rendering;

import ru.jgems3d.engine.graphics.opengl.rendering.debug.LinesDebugDraw;

public abstract class JGemsDebugGlobalConstants {
    public static float PATH_GEN_GRAPH_GAP = 1.0f;
    public static boolean FULL_BRIGHT;
    public static boolean SHOW_DEBUG_LINES;
    public static LinesDebugDraw linesDebugDraw = new LinesDebugDraw();

    static {
        JGemsDebugGlobalConstants.reset();
    }

    public static void reset() {
        JGemsDebugGlobalConstants.FULL_BRIGHT = false;
        JGemsDebugGlobalConstants.SHOW_DEBUG_LINES = false;
    }
}
