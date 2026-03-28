package api.scripting.coding.env.internal.util.world.render.table.fabrics;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSDirectRenderFabric", description = "Wrapper for DirectRenderFabric. Allows scripts to implement custom render factories and manage resources.")
public class JSDirectRenderFabric implements JSRenderFabricI {
    @JSCodingField(description = "Underlying Java DirectRenderFabric object")
    protected final DirectRenderFabric fabric;

    @JSCodingConstructor(description = "Constructs the JS wrapper for an existing DirectRenderFabric instance", paramNames = {"fabric"})
    public JSDirectRenderFabric(@NotNull DirectRenderFabric fabric) {
        this.fabric = fabric;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java DirectRenderFabric", paramNames = {})
    public DirectRenderFabric getJavaFabric() {
        return this.fabric;
    }

    @JSCodingFunctionOrMethod(description = "Allocate necessary GPU resources for a scene object", paramNames = {"jsSceneObject"})
    public void createResources(JSSceneObjectI jsSceneObject) {
        this.fabric.createResources(jsSceneObject.getJavaSceneObject());
    }

    @JSCodingFunctionOrMethod(description = "Release GPU resources of a scene object", paramNames = {"jsSceneObject"})
    public void destroyResources(JSSceneObjectI jsSceneObject) {
        this.fabric.destroyResources(jsSceneObject.getJavaSceneObject());
    }

    @JSCodingFunctionOrMethod(description = "Render all meshes of a 3D model at a given layer using the specified shader and renderer", paramNames = {"jsRenderer", "jsShader", "jsModel", "layer"})
    public void renderMeshList3D(JSOpenGLRenderer jsRenderer, JSShader jsShader, JSModel3D jsModel, int layer) {
        for (MeshNode3D<RenderMesh> meshNode3D : jsModel.getJavaModel3D().<MeshStructure3D<RenderMesh>>getMeshStructureCast().getNodes(layer)) {
            JGemsHelper.render().performDefaultModelMaterialOnShader(jsRenderer.getJavaRenderer().getWorld().getEnvironment(), jsShader.getJavaShaderManager(), meshNode3D.getMaterial());
            JGemsHelper.render().renderMeshNode(meshNode3D.getMeshData());
        }
    }

    @JSHideFromDoc
    @Override
    public IRenderFabric getJavaRenderFabric() {
        return this.fabric;
    }
}