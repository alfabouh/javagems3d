package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.data.IMeshUserData;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MeshStructure implements ICached {
    public static final String MESH_COLLISION_UD = "mesh_collision";
    public static final String MESH_RENDER_AABB_UD = "mesh_render_aabb";

    private final List<Animation> animationList;
    private final Map<String, IMeshUserData> meshUserData;

    public MeshStructure() {
        this.animationList = new ArrayList<>();
        this.meshUserData = new HashMap<>();
    }

    public abstract void clean();
    public abstract MeshRenderTarget getMeshTargetType();

    @SuppressWarnings("all")
    public MeshStructure loadAnimations(List<Animation> animations) {
        this.getAnimationList().clear();
        this.getAnimationList().addAll(animations);
        return this;
    }

    public int getAnimationsNum() {
        return this.getAnimationList().size();
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clean();
    }

    public List<Animation> getAnimationList() {
        return this.animationList;
    }

    @SuppressWarnings("all")
    public <T extends IMeshUserData> T getUnSafeMeshUserData(String key) {
        return this.getMeshUserData(key, null);
    }

    @SuppressWarnings("all")
    public <T extends IMeshUserData> T getMeshUserData(String key, Class<T> tClass) {
        if (this.getMeshUserData(key) == null) {
            return null;
        }
        if (tClass == null || this.getMeshUserData(key).getClass().isAssignableFrom(tClass)) {
            return (T) this.getMeshUserData(key);
        }
        return null;
    }

    public IMeshUserData getMeshUserData(String key) {
        return this.meshUserData.get(key);
    }

    public void setMeshUserData(String key, IMeshUserData meshUserData) {
        this.meshUserData.put(key, meshUserData);
    }
}