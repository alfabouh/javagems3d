package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.udata.IMeshUserData;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;

import java.util.*;

public abstract class MeshStructure <T extends MeshStructure.Node<? extends IMesh>> implements ICached {
    public static final String MESH_COLLISION_UD = "mesh_collision";

    private final List<T> meshNodes;
    private final List<Animation> animationList;
    private final Map<String, IMeshUserData> meshUserData;

    public MeshStructure() {
        this.meshNodes = new ArrayList<>();
        this.animationList = new ArrayList<>();
        this.meshUserData = new HashMap<>();
    }

    public MeshStructure(List<T> nodes) {
        this();
        this.getMeshNodes().addAll(nodes);
    }

    @SafeVarargs
    public MeshStructure(T... t) {
        this();
        this.getMeshNodes().addAll(Arrays.asList(t));
    }

    public abstract MeshDataType getMeshDataType();

    public boolean hasNodes() {
        return !this.getMeshNodes().isEmpty();
    }

    public T getFirstNode() {
        return this.getMeshNodes().get(0);
    }

    public void putMeshNode(T node) {
        this.getMeshNodes().add(node);
    }

    @SuppressWarnings("all")
    public MeshStructure loadAnimations(List<Animation> animations) {
        this.getAnimationList().clear();
        this.getAnimationList().addAll(animations);
        return this;
    }

    public boolean isAnimationsNotEmpty() {
        return this.getAnimationsNum() != 0;
    }

    public int getAnimationsNum() {
        return this.getAnimationList().size();
    }

    @Override
    public void onCleaningCache(ResourceCache resourceCache) {
        this.clear();
    }

    public void clear() {
        this.getMeshNodes().forEach(Node::clearNode);
        this.getMeshNodes().clear();
    }

    public List<T> getMeshNodes() {
        return this.meshNodes;
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

    public abstract static class Node <T extends IMesh> {
        private final T mesh;

        protected Node(T mesh) {
            this.mesh = mesh;
        }

        public void clearNode() {
            this.getMesh().clearMesh();
        }

        public T getMesh() {
            return this.mesh;
        }
    }
}