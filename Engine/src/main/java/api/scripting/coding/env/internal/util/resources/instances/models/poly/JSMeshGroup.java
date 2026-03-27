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
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;

import java.util.List;

@JSCodingClass(binding = "JSMeshGroup", description = "Represents a group of 3D meshes, exposing access to the underlying MeshGroup and supporting caching.")
public class JSMeshGroup implements JSCanBeCachedInMemory, JSMeshStructure3D {
    @JSHideFromDoc
    private final MeshGroup meshGroup;

    public JSMeshGroup(MeshGroup meshGroup) {
        this.meshGroup = meshGroup;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying MeshGroup instance.")
    public MeshGroup getJavaMeshGroup() {
        return this.meshGroup;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public List<JSMeshNode3D> getSolidNodes() {
        return this.meshGroup.getSolidNodes().stream().map(JSMeshNode3D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public List<JSMeshNode3D> getBlendedTransparencyNodes() {
        return this.meshGroup.getBlendedTransparencyNodes().stream().map(JSMeshNode3D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void loadAnimations(List<JSAnimation> animations) {
        this.meshGroup.loadAnimations(animations.stream().map(JSAnimation::getJavaAnimation).toList());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setLinkedMeshBuffer(JSMeshBuffer linkedMeshBuffer) {
        this.meshGroup.setLinkedMeshBuffer(linkedMeshBuffer.getJavaMeshBuffer());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void clearLinkedMeshBufferNodesData() {
        this.meshGroup.clearLinkedMeshBufferNodesData();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSMeshBuffer getLinkedMeshBuffer() {
        return new JSMeshBuffer(this.meshGroup.getLinkedMeshBuffer());
    }


    @JSCodingFunctionOrMethod(description = "...")
    public boolean canBeUsedInIndirectRendering() {
        return this.meshGroup.canBeUsedInIndirectRendering();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void clear() {
        this.meshGroup.clear();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean hasTransparency() {
        return this.meshGroup.hasTransparency();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isAnimatedStructure() {
        return this.meshGroup.isAnimatedStructure();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isAnimationsNotEmpty() {
        return this.meshGroup.isAnimationsNotEmpty();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public int getAnimationsNum() {
        return this.meshGroup.getAnimationsNum();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public List<JSAnimation> getAnimationsList() {
        return this.meshGroup.getAnimationsList().stream().map(JSAnimation::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setMeshAABBDataForAnimationFrame(JSAnimation animation, JSMeshBoundingBox meshBoundingBoxData) {
        this.meshGroup.setMeshAABBDataForAnimationFrame(animation.getJavaAnimation(), meshBoundingBoxData.getJavaMeshBoundingBoxData());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSMeshBoundingBox getMeshAABBDataForAnimation(JSAnimation animation) {
        return new JSMeshBoundingBox(this.meshGroup.getMeshAABBDataForAnimation(animation.getJavaAnimation()));
    }

    public JSMeshBoundingBox getBoundingBox() {
        return new JSMeshBoundingBox(this.meshGroup.getMeshAABBData());
    }

    public MeshCollisionData getJavaMeshCollisionData() {
        return this.meshGroup.getMeshCollisionData();
    }

    @JSCodingFunctionOrMethod(description = "Get Java object.")
    @Override
    public MeshStructure3D<? extends IMesh> getJavaMeshStructure3D() {
        return this.getJavaMeshGroup();
    }
}