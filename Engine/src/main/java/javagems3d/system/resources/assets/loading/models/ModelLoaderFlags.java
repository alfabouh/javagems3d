package javagems3d.system.resources.assets.loading.models;

public abstract  class ModelLoaderFlags {
    public static final int
            CREATE_COLLISION_UD = 1 << 2,
            LOAD_ANIMATIONS = 1 << 3,
            CREATE_AABB_UD = 1 << 4;

    public static final int DEFAULT = ModelLoaderFlags.CREATE_COLLISION_UD | ModelLoaderFlags.CREATE_AABB_UD;
    public static final int ALL = ModelLoaderFlags.CREATE_COLLISION_UD | ModelLoaderFlags.LOAD_ANIMATIONS | ModelLoaderFlags.CREATE_AABB_UD;
}
