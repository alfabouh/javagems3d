package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.cache.ICached;
import javagems3d.system.resources.cache.ResourceCache;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MeshStructure3D<T extends IMesh> extends MeshStructure<T, MeshNode3D<T>> {
    public static final int SOLID_LAYER = 0;
    public static final int TRANSPARENCY_LAYER = 1;
    private final List<Animation> animationsList;

    private MetaData metaData;

    public MeshStructure3D() {
        this.animationsList = new ArrayList<>();
        this.metaData = new MetaData();
    }

    public abstract boolean canBeUsedInIndirectRendering();

    public int @NotNull [] getLayersToInit() {
        return new int[] {MeshStructure3D.SOLID_LAYER, MeshStructure3D.TRANSPARENCY_LAYER};
    }

    public void loadAnimations(List<Animation> animations) {
        this.getAnimationsList().addAll(animations);
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
        this.metaData.clear();
        this.getAnimationsList().forEach(Animation::clear);
        this.getAnimationsList().clear();
    }

    @SuppressWarnings("all")
    public <E extends IUserData> E getUnSafeMeshUserData(String key) {
        return this.getMeshUserData(key, null);
    }

    @SuppressWarnings("all")
    public <E extends IUserData> E getMeshUserData(String key, Class<E> tClass) {
        if (this.getMeshUserData(key) == null) {
            return null;
        }
        if (tClass == null || this.getMeshUserData(key).getClass().isAssignableFrom(tClass)) {
            return (E) this.getMeshUserData(key);
        }
        return null;
    }

    public MeshStructure3D<T> setMetaData(@NotNull MetaData metaData) {
        this.metaData = metaData;
        return this;
    }

    public void copyMetaData(MeshStructure3D<?> from) {
        this.metaData = new MetaData(this, from.metaData);
    }

    public void setMeshAABBDataForAnimationFrame(Animation animation, MeshBoundingBoxData meshBoundingBoxData) {
        this.getMetaData().animationsBoundingBoxes.put(animation, meshBoundingBoxData);
    }

    public MeshBoundingBoxData getMeshAABBDataForAnimation(Animation animation) {
        return this.getMetaData().animationsBoundingBoxes.get(animation);
    }

    public void setMeshAABBData(MeshBoundingBoxData meshBoundingBoxData) {
        this.getMetaData().setMeshBoundingBox(meshBoundingBoxData);
    }

    public void setMeshCollisionData(MeshCollisionData meshCollisionData) {
        this.getMetaData().setMeshCollisionData(meshCollisionData);
    }

    public @NotNull MetaData getMetaData() {
        return this.metaData;
    }

    public MeshCollisionData getMeshCollisionData() {
        return this.getMetaData().getMeshCollisionData();
    }

    public MeshBoundingBoxData getMeshAABBData() {
        return this.getMetaData().getMeshBoundingBox();
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

    public static class MetaData implements ICached {
        private MeshCollisionData meshCollisionData;
        private MeshBoundingBoxData meshBoundingBox;
        private Map<Animation, MeshBoundingBoxData> animationsBoundingBoxes;

        public MetaData() {
            this.meshBoundingBox = null;
            this.meshCollisionData = null;
            this.animationsBoundingBoxes = new HashMap<>();
        }

        public MetaData(@NotNull MeshStructure3D<?> copyFor, @NotNull MetaData copy) {
            this.meshBoundingBox = copy.meshBoundingBox.copy();
            if (copy.meshCollisionData != null) {
                this.meshCollisionData = copy.meshCollisionData.copyFor(copyFor);
            }
            this.animationsBoundingBoxes = new HashMap<>();
            copy.animationsBoundingBoxes.forEach((key, value) -> {
               this.animationsBoundingBoxes.put(key, value.copy());
            });
        }

        public MetaData copyFor(MeshStructure3D<?> copyFor) {
            return new MetaData(copyFor, this);
        }

        public void clear() {
            this.meshBoundingBox = null;
            this.meshCollisionData = null;
            this.animationsBoundingBoxes = null;
        }

        public @Nullable MeshCollisionData getMeshCollisionData() {
            return this.meshCollisionData;
        }

        public MetaData setMeshCollisionData(MeshCollisionData meshCollisionData) {
            this.meshCollisionData = meshCollisionData;
            return this;
        }

        public MeshBoundingBoxData getMeshBoundingBox() {
            return this.meshBoundingBox;
        }

        public MetaData setMeshBoundingBox(MeshBoundingBoxData meshBoundingBox) {
            this.meshBoundingBox = meshBoundingBox;
            return this;
        }

        public Map<Animation, MeshBoundingBoxData> getAnimationsBoundingBoxes() {
            return this.animationsBoundingBoxes;
        }

        public MeshBoundingBoxData getMeshAABBDataForAnimation(Animation animation) {
            return this.animationsBoundingBoxes.get(animation);
        }

        public void setMeshAABBDataForAnimationFrame(Animation animation, MeshBoundingBoxData meshBoundingBoxData) {
            this.animationsBoundingBoxes.put(animation, meshBoundingBoxData);
        }

        public MetaData setAnimationsBoundingBoxes(Map<Animation, MeshBoundingBoxData> animationsBoundingBoxes) {
            this.animationsBoundingBoxes = animationsBoundingBoxes;
            return this;
        }

        @Override
        public void onClearingCache(ResourceCache resourceCache) {
            this.clear();
        }
    }
}
