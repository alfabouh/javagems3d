package api.scripting.coding.env.internal.util.resources.instances.models.poly;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimation;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.bound.JSMeshBoundingBox;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode3D;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;

import java.util.List;

@JSCodingClass(binding = "JSMeshGroup", description = "Wrapper for a group of 3D meshes, exposing nodes, animations, AABB, linked buffers and caching.")
public class JSMeshGroup implements JSCanBeCachedInMemory, JSMeshStructure3D {
    @JSHideFromDoc
    private final MeshGroup meshGroup;

    public JSMeshGroup(MeshGroup meshGroup) {
        this.meshGroup = meshGroup;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying MeshGroup instance (unsafe).")
    public MeshGroup getJavaMeshGroup() {
        return this.meshGroup;
    }

    @JSCodingFunctionOrMethod(description = "Get all solid (opaque) 3D mesh nodes in this group.")
    public List<JSMeshNode3D> getSolidNodes() {
        return this.meshGroup.getSolidNodes().stream().map(JSMeshNode3D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "Get all 3D mesh nodes with blended transparency.")
    public List<JSMeshNode3D> getBlendedTransparencyNodes() {
        return this.meshGroup.getBlendedTransparencyNodes().stream().map(JSMeshNode3D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "Load a list of animations into this mesh group.")
    public void loadAnimations(List<JSAnimation> animations) {
        this.meshGroup.loadAnimations(animations.stream().map(JSAnimation::getJavaAnimation).toList());
    }

    @JSCodingFunctionOrMethod(description = "Check if this mesh group can be used with indirect rendering.")
    public boolean canBeUsedInIndirectRendering() {
        return this.meshGroup.canBeUsedInIndirectRendering();
    }

    @JSCodingFunctionOrMethod(description = "Clear all mesh group data, including nodes and animations.")
    public void clear() {
        this.meshGroup.clear();
    }

    @JSCodingFunctionOrMethod(description = "Check if any node in the group has transparency.")
    public boolean hasTransparency() {
        return this.meshGroup.hasTransparency();
    }

    @JSCodingFunctionOrMethod(description = "Check if this group contains an animated structure.")
    public boolean isAnimatedStructure() {
        return this.meshGroup.isAnimatedStructure();
    }

    @JSCodingFunctionOrMethod(description = "Check if animations list is not empty.")
    public boolean isAnimationsNotEmpty() {
        return this.meshGroup.isAnimationsNotEmpty();
    }

    @JSCodingFunctionOrMethod(description = "Get the total number of animations in this mesh group.")
    public int getAnimationsNum() {
        return this.meshGroup.getAnimationsNum();
    }

    @JSCodingFunctionOrMethod(description = "Get the list of animations contained in this mesh group.")
    public List<JSAnimation> getAnimationsList() {
        return this.meshGroup.getAnimationsList().stream().map(JSAnimation::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "Set bounding box data for a specific animation frame.")
    public void setMeshAABBDataForAnimationFrame(JSAnimation animation, JSMeshBoundingBox meshBoundingBoxData) {
        this.meshGroup.setMeshAABBDataForAnimationFrame(animation.getJavaAnimation(), meshBoundingBoxData.getJavaMeshBoundingBoxData());
    }

    @JSCodingFunctionOrMethod(description = "Get normalized bounding box for a specific animation.")
    public JSMeshBoundingBox getMeshAABBDataForAnimation(JSAnimation animation) {
        return new JSMeshBoundingBox(this.meshGroup.getMeshAABBDataForAnimation(animation.getJavaAnimation()));
    }

    @JSCodingFunctionOrMethod(description = "Get the overall bounding box of this mesh group.")
    public JSMeshBoundingBox getBoundingBox() {
        return new JSMeshBoundingBox(this.meshGroup.getMeshAABBData());
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying mesh collision data.")
    public MeshCollisionData getJavaMeshCollisionData() {
        return this.meshGroup.getMeshCollisionData();
    }

    @JSCodingFunctionOrMethod(description = "Get Java MeshStructure3D object.")
    @Override
    public MeshStructure3D<RenderMesh> getJavaMeshStructure3D() {
        return this.getJavaMeshGroup();
    }
}