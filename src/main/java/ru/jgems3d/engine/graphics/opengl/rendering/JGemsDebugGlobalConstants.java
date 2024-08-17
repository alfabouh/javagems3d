package ru.jgems3d.engine.graphics.opengl.rendering;

import ru.jgems3d.engine.graphics.opengl.rendering.debug.LinesDebugDraw;

public abstract class JGemsDebugGlobalConstants {
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
