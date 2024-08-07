package ru.jgems3d.engine.graphics.opengl.rendering.debug;

public abstract class GlobalRenderDebugConstants {
    public static boolean FULL_BRIGHT;
    public static boolean SHOW_DEBUG_LINES;

    static {
        GlobalRenderDebugConstants.reset();
    }

    public static void reset() {
        GlobalRenderDebugConstants.FULL_BRIGHT = false;
        GlobalRenderDebugConstants.SHOW_DEBUG_LINES = false;
    }
}
