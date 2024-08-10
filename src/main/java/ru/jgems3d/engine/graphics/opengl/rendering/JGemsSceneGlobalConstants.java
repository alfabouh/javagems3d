package ru.jgems3d.engine.graphics.opengl.rendering;

public abstract class JGemsSceneGlobalConstants {
    //section Particles
    public static int MAX_PARTICLES = 256;

    //section Screen
    public static final double RENDER_TICKS_UPD_RATE = 60.0d;

    //section Window
    public static final int defaultW = 1280;
    public static final int defaultH = 720;

    //section Light
    public static final int MAX_POINT_LIGHTS = 128;
    public static final int MAX_POINT_LIGHTS_SHADOWS = 3;

    //section SSAO
    public static int SSAO_NOISE_SIZE = 4;

    //section Projection
    public static float FOV = (float) Math.toRadians(60.0f);
    public static float Z_NEAR = 0.1f;
    public static float Z_FAR = 100.0f;

    //section Shadows
    public static final int CASCADE_SPLITS = 3;
    public static float MAX_ALPHA_TO_CULL_SHADOW = 0.5f;
    public static float MAX_ALPHA_TO_DISCARD_SHADOW_PIXEL = 0.75f;

    //section IMGUI
    public static int MAX_TICKS_TO_REMOVE_UNUSED_UI = 3;
    public static int GLOBAL_UI_SCALING = 0;
    public static boolean AUTO_SCREEN_SCALING = false;
}
