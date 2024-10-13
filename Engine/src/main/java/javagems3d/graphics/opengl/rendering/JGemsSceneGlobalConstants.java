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

package javagems3d.graphics.opengl.rendering;

import javagems3d.JGems3D;
import org.joml.Vector2f;

public abstract class JGemsSceneGlobalConstants {
    //section Screen
    public static final double RENDER_TICKS_UPD_RATE = 60.0d;
    //section WINDOW
    public static final int defaultW = 1280;
    public static final int defaultH = 720;
    //section Light
    public static final int MAX_POINT_LIGHTS = 128;
    public static final int MAX_POINT_LIGHTS_SHADOWS = 3;
    //section Shadows
    public static final int CASCADE_SPLITS = 3;
    //section Particles
    public static int MAX_PARTICLES = 256;
    //section SSAO
    public static int SSAO_NOISE_SIZE = 4;
    //section Projection
    public static float FOV = (float) Math.toRadians(60.0f);
    public static float Z_NEAR = 0.1f;
    public static float Z_FAR = (float) JGems3D.MAP_MAX_SIZE * 2.0f;


    public static float EVSM_POSITIVE_EXPONENT = 60.0f;
    public static float EVSM_NEGATIVE_EXPONENT = 5.0f;
    public static float MAX_ALPHA_TO_CULL_SHADOW = 0.5f;
    public static float MAX_ALPHA_TO_DISCARD_SHADOW_FRAGMENT = 0.75f;
    public static int MAX_SHADOW_RES = 1024;
    public static boolean DRAW_BACK_FACES_FOR_SHADOWS = true;

    public static final Vector2f NEUTRAL_SHADOWS = new Vector2f();

    static {
        float positiveExponent = JGemsSceneGlobalConstants.EVSM_POSITIVE_EXPONENT;
        float negativeExponent = JGemsSceneGlobalConstants.EVSM_NEGATIVE_EXPONENT;
        Vector2f exponents = new Vector2f(positiveExponent, negativeExponent);
        float pos = (float) Math.exp(exponents.x);
        float neg = (float) -Math.exp(-exponents.y);
        JGemsSceneGlobalConstants.NEUTRAL_SHADOWS.set(pos, neg);
    }

    //section IMGUI
    public static int TICKS_TO_CLEAN_UNUSED_UI = 3;
    public static int GLOBAL_UI_SCALING = 0;
    public static boolean AUTO_SCREEN_SCALING = false;

    //section Render
    public static float HDR_EXPOSURE = 2.5f;
    public static float HDR_GAMMA = 0.3f;

    public static float SSAO_RADIUS = 1.5f;
    public static float SSAO_BIAS = 0.025f;
    public static float SSAO_RANGE = 5.0f;

    public static boolean USE_HDR = true;
    public static boolean USE_SSAO = true;
    public static boolean USE_BLOOM = true;
    public static boolean USE_FXAA = true;
    public static boolean USE_SHADOWS = true;
}
