package javagems3d.global;

public abstract class JGemsGlobalConfiguration {
    public static final int DEFAULT_SCREEN_WIDTH = 1280;
    public static final int DEFAULT_SCREEN_HEIGHT = 720;

    public static final double RENDER_TICKS_UPD_RATE = 60.0d;
    public static final int MAX_POINT_LIGHTS = 128;
    public static final int MAX_POINT_LIGHTS_SHADOWS = 3;

    public static int INDIRECT_RENDERING_MATERIALS_PACK_SIZE = 9 + (3);
    public static int INDIRECT_RENDERING_PROPERTIES_PACK_SIZE = 3;

    public static int MAX_INDIRECT_RENDERING_MESH_MATERIALS = 256;
    public static int MAX_INDIRECT_RENDERING_MESH_DATASETS = 1024;
    public static int MAX_BINDLESS_TEXTURES = 1024;

    public static int MAX_PARTICLES = 512;
}