package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GLTF2Primitive {
    private CullingAABB localCullingAABB;
    private final GLTF2Accessor<Float> POSITION;
    private final GLTF2Accessor<Float> NORMAL;
    private final GLTF2Accessor<Float> TEXCOORD_0;
    private final GLTF2Accessor<Float> TANGENT;
    private final GLTF2Accessor<Float> BiTANGENT;
    private final GLTF2Accessor<Integer> JOINTS_0;
    private final GLTF2Accessor<Float> WEIGHTS_0;

    private final int materialId;
    private final GLTF2Accessor<Integer> indices;

    public GLTF2Primitive(@NotNull GLTF2Accessor<Float> POSITION, @NotNull GLTF2Accessor<Float> NORMAL, @Nullable GLTF2Accessor<Float> TEXCOORD_0, @NotNull GLTF2Accessor<Float> TANGENT, @NotNull GLTF2Accessor<Float> BiTANGENT, @Nullable GLTF2Accessor<Integer> JOINTS_0, @Nullable GLTF2Accessor<Float> WEIGHTS_0, int materialId, @NotNull GLTF2Accessor<Integer> indices) {
        this.localCullingAABB = null;
        this.POSITION = POSITION;
        this.NORMAL = NORMAL;
        this.TEXCOORD_0 = TEXCOORD_0;
        this.TANGENT = TANGENT;
        this.BiTANGENT = BiTANGENT;
        this.JOINTS_0 = JOINTS_0;
        this.WEIGHTS_0 = WEIGHTS_0;
        this.materialId = materialId;
        this.indices = indices;
    }

    public GLTF2Primitive setLocalCullingAABB(CullingAABB localCullingAABB) {
        this.localCullingAABB = localCullingAABB;
        return this;
    }

    public CullingAABB getLocalCullingAABB() {
        return this.localCullingAABB;
    }

    public GLTF2Accessor<Float> getPOSITION() {
        return this.POSITION;
    }

    public GLTF2Accessor<Float> getNORMAL() {
        return this.NORMAL;
    }

    public GLTF2Accessor<Float> getTEXCOORD_0() {
        return this.TEXCOORD_0;
    }

    public GLTF2Accessor<Float> getTANGENT() {
        return this.TANGENT;
    }

    public GLTF2Accessor<Float> getBiTANGENT() {
        return this.BiTANGENT;
    }

    public GLTF2Accessor<Integer> getJOINTS_0() {
        return this.JOINTS_0;
    }

    public GLTF2Accessor<Float> getWEIGHTS_0() {
        return this.WEIGHTS_0;
    }

    public int getMaterialId() {
        return this.materialId;
    }

    public GLTF2Accessor<Integer> getIndices() {
        return indices;
    }
}
