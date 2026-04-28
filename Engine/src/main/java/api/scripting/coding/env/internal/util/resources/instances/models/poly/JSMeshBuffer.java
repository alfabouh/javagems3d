package api.scripting.coding.env.internal.util.resources.instances.models.poly;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimation;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.bound.JSMeshBoundingBox;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode3D;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;

import java.util.List;

@JSCodingClass(binding = "JSMeshBuffer", description = "Wrapper for a 3D mesh buffer, providing access to nodes, animations, AABB and caching control.")
public class JSMeshBuffer implements JSCanBeCachedInMemory, JSMeshStructure3D {
    @JSHideFromDoc
    private final MeshBuffer meshBuffer;

    public JSMeshBuffer(MeshBuffer meshBuffer) {
        this.meshBuffer = meshBuffer;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java MeshBuffer object (unsafe).")
    public MeshBuffer getJavaMeshBuffer() {
        return this.meshBuffer;
    }

    @JSCodingFunctionOrMethod(description = "Get all solid (opaque) 3D mesh nodes in this buffer.")
    public List<JSMeshNode3D> getSolidNodes() {
        return this.meshBuffer.getSolidNodes().stream().map(JSMeshNode3D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "Get all 3D mesh nodes with blended transparency.")
    public List<JSMeshNode3D> getBlendedTransparencyNodes() {
        return this.meshBuffer.getBlendedTransparencyNodes().stream().map(JSMeshNode3D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "Load a list of animations into this mesh buffer.")
    public void loadAnimations(List<JSAnimation> animations) {
        this.meshBuffer.loadAnimations(animations.stream().map(JSAnimation::getJavaAnimation).toList());
    }

    @JSCodingFunctionOrMethod(description = "Check if this mesh buffer can be used with indirect rendering.")
    public boolean canBeUsedInIndirectRendering() {
        return this.meshBuffer.canBeUsedInIndirectRendering();
    }

    @JSCodingFunctionOrMethod(description = "Clear all mesh buffer data, including nodes and animations.")
    public void clear() {
        this.meshBuffer.clear();
    }

    @JSCodingFunctionOrMethod(description = "Check if mesh nodes are kept in memory even when not used.")
    public boolean iskeepTrianglesInMemory() {
        return this.meshBuffer.isKeepTrianglesInMemory();
    }

    @JSCodingFunctionOrMethod(description = "Set whether to keep mesh nodes in memory.")
    public void setkeepTrianglesInMemory(boolean keepTrianglesInMemory) {
        this.meshBuffer.setKeepTrianglesInMemory(keepTrianglesInMemory);
    }

    @JSCodingFunctionOrMethod(description = "Clear mesh nodes data without removing the nodes themselves.", paramNames = {"keepTriangleInMem"})
    public void clearNodesData(boolean keepTriangleInMem) {
        this.meshBuffer.clearNodesData(keepTriangleInMem);
    }

    @JSCodingFunctionOrMethod(description = "Check if any node in the buffer has transparency.")
    public boolean hasTransparency() {
        return this.meshBuffer.hasTransparency();
    }

    @JSCodingFunctionOrMethod(description = "Check if this buffer contains an animated structure.")
    public boolean isAnimatedStructure() {
        return this.meshBuffer.isAnimatedStructure();
    }

    @JSCodingFunctionOrMethod(description = "Check if animations list is not empty.")
    public boolean isAnimationsNotEmpty() {
        return this.meshBuffer.isAnimationsNotEmpty();
    }

    @JSCodingFunctionOrMethod(description = "Get the total number of animations in this mesh buffer.")
    public int getAnimationsNum() {
        return this.meshBuffer.getAnimationsNum();
    }

    @JSCodingFunctionOrMethod(description = "Get the list of animations contained in this mesh buffer.")
    public List<JSAnimation> getAnimationsList() {
        return this.meshBuffer.getAnimationsList().stream().map(JSAnimation::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "Set bounding box data for a specific animation frame.")
    public void setMeshAABBDataForAnimationFrame(JSAnimation animation, JSMeshBoundingBox meshBoundingBoxData) {
        this.meshBuffer.setMeshAABBDataForAnimationFrame(animation.getJavaAnimation(), meshBoundingBoxData.getJavaMeshBoundingBoxData());
    }

    @JSCodingFunctionOrMethod(description = "Get normalized bounding box for a specific animation.")
    public JSMeshBoundingBox getMeshAABBDataForAnimation(JSAnimation animation) {
        return new JSMeshBoundingBox(this.meshBuffer.getMeshAABBDataForAnimation(animation.getJavaAnimation()));
    }

    @JSCodingFunctionOrMethod(description = "Get the overall bounding box of this mesh buffer.")
    public JSMeshBoundingBox getBoundingBox() {
        return new JSMeshBoundingBox(this.meshBuffer.getMeshAABBData());
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying mesh collision data.")
    public MeshCollisionData getJavaMeshCollisionData() {
        return this.meshBuffer.getMeshCollisionData();
    }

    @JSCodingFunctionOrMethod(description = "Get Java MeshStructure3D object.")
    @Override
    public MeshStructure3D<DataMesh> getJavaMeshStructure3D() {
        return this.getJavaMeshBuffer();
    }
}