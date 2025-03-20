package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.udata.IMeshUserData;
import javagems3d.system.resources.assets.models.mesh.udata.MeshAABBData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MeshStructure3D<T extends IMesh> extends MeshStructure<T, MeshNode3D<T>> {
    public static final int SOLID_LAYER = 0;
    public static final int TRANSPARENCY_LAYER = 1;

    public static final String MESH_COLLISION_UD = "mesh_collision";

    private final Map<String, IMeshUserData> meshUserData;
    private final List<Animation> animationsList;

    private MeshAABBData meshAABBData;
    private final Map<Animation, MeshAABBData> frameMeshAABBDataMap;

    public MeshStructure3D() {
        this.meshUserData = new HashMap<>();
        this.animationsList = new ArrayList<>();

        this.meshAABBData = null;
        this.frameMeshAABBDataMap = new HashMap<>();
    }

    public abstract boolean canBeUsedInIndirectRendering();

    public int @NotNull [] getLayersToInit() {
        return new int[] {MeshStructure3D.SOLID_LAYER, MeshStructure3D.TRANSPARENCY_LAYER};
    }

    public void loadAnimations(List<Animation> animations) {
        this.getAnimationsList().addAll(animations);
    }

    public void setMeshUserData(String key, IMeshUserData meshUserData) {
        this.meshUserData.put(key, meshUserData);
    }

    public void putNodes(List<MeshNode3D<T>> list) {
        for (MeshNode3D<T> m : list) {
            this.putNode(MeshStructure3D.chooseLayer(m.getMaterial()), m);
        }
    }

    public void putSolidNodes(List<MeshNode3D<T>> list) {
        for (MeshNode3D<T> m : list) {
            this.putNode(MeshStructure3D.SOLID_LAYER, m);
        }
    }

    public void putBlendedTransparencyNodes(List<MeshNode3D<T>> list) {
        for (MeshNode3D<T> m : list) {
            this.putNode(MeshStructure3D.TRANSPARENCY_LAYER, m);
        }
    }

    public void putSolidNode(MeshNode3D<T> meshNode3D) {
        this.putNode(MeshStructure3D.SOLID_LAYER, meshNode3D);
    }

    public void putBlendedTransparencyNode(MeshNode3D<T> meshNode3D) {
        this.putNode(MeshStructure3D.TRANSPARENCY_LAYER, meshNode3D);
    }

    public List<MeshNode3D<T>> getSolidNodes() {
        return this.getNodes(MeshStructure3D.SOLID_LAYER);
    }

    public List<MeshNode3D<T>> getBlendedTransparencyNodes() {
        return this.getNodes(MeshStructure3D.TRANSPARENCY_LAYER);
    }

    public boolean hasTransparency() {
        return !this.getBlendedTransparencyNodes().isEmpty();
    }

    public void clear() {
        super.clear();
        this.meshUserData.clear();
        this.frameMeshAABBDataMap.clear();
        this.getAnimationsList().forEach(Animation::clear);
        this.getAnimationsList().clear();
    }

    @SuppressWarnings("all")
    public <E extends IMeshUserData> E getUnSafeMeshUserData(String key) {
        return this.getMeshUserData(key, null);
    }

    @SuppressWarnings("all")
    public <E extends IMeshUserData> E getMeshUserData(String key, Class<E> tClass) {
        if (this.getMeshUserData(key) == null) {
            return null;
        }
        if (tClass == null || this.getMeshUserData(key).getClass().isAssignableFrom(tClass)) {
            return (E) this.getMeshUserData(key);
        }
        return null;
    }

    public void setMeshAABBDataForAnimationFrame(Animation animation, MeshAABBData meshAABBData) {
        this.frameMeshAABBDataMap.put(animation, meshAABBData);
    }

    public MeshAABBData getMeshAABBDataForAnimation(Animation animation) {
        return this.frameMeshAABBDataMap.get(animation);
    }

    public MeshStructure3D<T> setMeshAABBData(MeshAABBData meshAABBData) {
        this.meshAABBData = meshAABBData;
        return this;
    }

    public MeshAABBData getMeshAABBData() {
        return this.meshAABBData;
    }

    public boolean isAnimatedStructure() {
        return this.isAnimationsNotEmpty();
    }

    public boolean isAnimationsNotEmpty() {
        return this.getAnimationsNum() != 0;
    }

    public int getAnimationsNum() {
        return this.getAnimationsList().size();
    }

    public List<Animation> getAnimationsList() {
        return this.animationsList;
    }

    public IMeshUserData getMeshUserData(String key) {
        return this.meshUserData.get(key);
    }

    public boolean hasMeshUserData(String key) {
        return this.getMeshUserData(key) != null;
    }
}
