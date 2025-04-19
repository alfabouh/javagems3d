package javagems3d.system.global;

import javagems3d.JGems3D;
import org.joml.Vector2f;

public abstract class JGemsConfig {
    public static abstract class DEBUG {
        public static float PATH_GEN_GRAPH_GAP = 1.0f;
        public static boolean FULL_BRIGHT;
        public static boolean SHOW_DEBUG_LINES;

        static {
            reset();
        }

        public static void reset() {
            FULL_BRIGHT = false;
            SHOW_DEBUG_LINES = false;
        }
    }

    public static abstract class SYSTEM {
        public static final int DEFAULT_SCREEN_WIDTH = 1280;
        public static final int DEFAULT_SCREEN_HEIGHT = 720;
        public static final double RENDER_TICKS_UPD_RATE = 60.0d;
        public static float FOV = (float) Math.toRadians(60.0f);
        public static float Z_NEAR = 0.1f;
        public static float Z_FAR = (float) JGems3D.MAP_MAX_SIZE * 2.0f;
        public static float DEFAULT_ALPHA_DISCARD = 0.7f;

        public static final Vector2f NEUTRAL_SHADOWS = new Vector2f();
        public static final int MAX_POINT_LIGHTS = 128;
        public static final int MAX_POINT_LIGHTS_SHADOWS = 3;
        public static final int SUN_SHADOW_CASCADES = 3;
        public static int MAX_SHADOW_RES = 2048;
        public static float EVSM_POSITIVE_EXPONENT = 60.0f;
        public static float EVSM_NEGATIVE_EXPONENT = 5.0f;
        public static float MAX_ALPHA_TO_DISCARD_SHADOW_FRAGMENT = DEFAULT_ALPHA_DISCARD;
        public static boolean CAST_SHADOWS_FROM_TRANSPARENT_MESHES = true;
        public static boolean DRAW_BACK_FACES_FOR_SHADOWS = true;


        public static int POINT_LIGHT_STRUCT_SIZE = 11 + (2) + (3);
        public static int POINT_LIGHT_BUFFER_PACK_SIZE = (SYSTEM.POINT_LIGHT_STRUCT_SIZE * SYSTEM.MAX_POINT_LIGHTS);
        public static int SUN_LIGHT_BUFFER_PACK_SIZE = 16;
        public static int FOG_BUFFER_PACK_SIZE = 8 + (1);

        public static int INDIRECT_RENDERING_MATERIALS_PACK_SIZE = 15 + (1) + 1;
        public static int INDIRECT_RENDERING_PROPERTIES_PACK_SIZE = 1;

        public static int MAX_VERTEXES_IN_MODEL = (int) Math.pow(2, 22);

        public static int MAX_INDIRECT_RENDERING_MESH_PROPERTIES = 512;
        public static int MAX_INDIRECT_RENDERING_MESH_MATERIALS = 256;
        public static int MAX_INDIRECT_RENDERING_MESH_DATASETS = 2048;
        public static int MAX_BINDLESS_TEXTURES = 1024;


        public static final float DEFAULT_ANIM_FPS = 24.0f;
        public static int ANIM_MAX_BONES = 64;
        public static int ANIM_MAX_WEIGHTS = 4;


        public static int MAX_PARTICLES = 512;
        public static float CAM_SENS = 0.0015f;


        public static int SSAO_NOISE_SIZE = 4;
        public static float SSAO_RADIUS = 0.25f;
        public static float SSAO_BIAS = 0.01f;
        public static float SSAO_RANGE = 1.25f;
        public static float HDR_EXPOSURE = 2.5f;
        public static float HDR_GAMMA = 0.3f;
        public static boolean USE_HDR = true;
        public static boolean USE_SSAO = true;
        public static boolean USE_BLOOM = true;
        public static boolean USE_FXAA = true;
        public static boolean USE_SHADOWS = true;


        public static int TICKS_TO_CLEAN_UNUSED_UI = 3;
        public static int GLOBAL_UI_SCALING = 0;
        public static boolean AUTO_SCREEN_SCALING = false;

        static {
            float positiveExponent = EVSM_POSITIVE_EXPONENT;
            float negativeExponent = EVSM_NEGATIVE_EXPONENT;
            Vector2f exponents = new Vector2f(positiveExponent, negativeExponent);
            float pos = (float) Math.exp(exponents.x);
            float neg = (float) -Math.exp(-exponents.y);
            NEUTRAL_SHADOWS.set(pos, neg);
        }
    }
}
