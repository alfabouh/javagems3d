package api.scripting.coding.env.internal.util.resources.instances.models.poly;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.JSCanBeCachedInMemory;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimation;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.bound.JSMeshBoundingBox;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode3D;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;

import java.util.List;

@JSCodingClass(binding = "JSMeshBuffer", description = "Wrapper for a 3D mesh buffer, allowing caching and access to the underlying Java MeshStructure3D object.")
public class JSMeshBuffer implements JSCanBeCachedInMemory, JSMeshStructure3D {
    @JSHideFromDoc
    private final MeshBuffer meshBuffer;

    public JSMeshBuffer(MeshBuffer meshBuffer) {
        this.meshBuffer = meshBuffer;
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying Java MeshBuffer object.")
    public MeshBuffer getJavaMeshBuffer() {
        return this.meshBuffer;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public List<JSMeshNode3D> getSolidNodes() {
        return this.meshBuffer.getSolidNodes().stream().map(JSMeshNode3D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public List<JSMeshNode3D> getBlendedTransparencyNodes() {
        return this.meshBuffer.getBlendedTransparencyNodes().stream().map(JSMeshNode3D::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void loadAnimations(List<JSAnimation> animations) {
        this.meshBuffer.loadAnimations(animations.stream().map(JSAnimation::getJavaAnimation).toList());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean canBeUsedInIndirectRendering() {
        return this.meshBuffer.canBeUsedInIndirectRendering();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void clear() {
        this.meshBuffer.clear();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isKeepNodesInMemory() {
        return this.meshBuffer.isKeepNodesInMemory();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setKeepNodesInMemory(boolean keepNodesInMemory) {
        this.meshBuffer.setKeepNodesInMemory(keepNodesInMemory);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void clearNodesData() {
        this.meshBuffer.clearNodesData();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean hasTransparency() {
        return this.meshBuffer.hasTransparency();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isAnimatedStructure() {
        return this.meshBuffer.isAnimatedStructure();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public boolean isAnimationsNotEmpty() {
        return this.meshBuffer.isAnimationsNotEmpty();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public int getAnimationsNum() {
        return this.meshBuffer.getAnimationsNum();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public List<JSAnimation> getAnimationsList() {
        return this.meshBuffer.getAnimationsList().stream().map(JSAnimation::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void setMeshAABBDataForAnimationFrame(JSAnimation animation, JSMeshBoundingBox meshBoundingBoxData) {
        this.meshBuffer.setMeshAABBDataForAnimationFrame(animation.getJavaAnimation(), meshBoundingBoxData.getJavaMeshBoundingBoxData());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSMeshBoundingBox getMeshAABBDataForAnimation(JSAnimation animation) {
        return new JSMeshBoundingBox(this.meshBuffer.getMeshAABBDataForAnimation(animation.getJavaAnimation()));
    }

    public JSMeshBoundingBox getBoundingBox() {
        return new JSMeshBoundingBox(this.meshBuffer.getMeshAABBData());
    }

    public MeshCollisionData getJavaMeshCollisionData() {
        return this.meshBuffer.getMeshCollisionData();
    }

    @JSCodingFunctionOrMethod(description = "Get Java object.")
    @Override
    public MeshStructure3D<? extends IMesh> getJavaMeshStructure3D() {
        return this.getJavaMeshBuffer();
    }
}