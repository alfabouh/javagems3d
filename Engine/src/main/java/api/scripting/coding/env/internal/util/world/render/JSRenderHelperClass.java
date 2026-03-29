package api.scripting.coding.env.internal.util.world.render;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.resources.instances.models.JSMaterial;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel2D;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode3D;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;
import javagems3d.graphics.objects.IAnimated;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.List;

@JSCodingClass(binding = "JSRenderHelperClass", description = "Helper class for rendering 2D/3D models, meshes, and performing shader operations.")
public class JSRenderHelperClass implements JSGlobalVarFactory<JSRenderHelperClass> {

    @JSCodingField(description = "Diffuse texture code for shaders.")
    public static final int DIFFUSE_CODE = 1 << 2;

    @JSCodingField(description = "Normals texture code for shaders.")
    public static final int NORMALS_CODE = 1 << 3;

    @JSCodingField(description = "Emission texture code for shaders.")
    public static final int EMISSION_CODE = 1 << 4;

    @JSCodingField(description = "Metallic-roughness texture code for shaders.")
    public static final int METALLIC_ROUGHNESS_CODE = 1 << 5;

    @JSCodingField(description = "Layer index for solid 3D models.")
    public static final int MODEL_SOLID_LAYER = MeshStructure3D.SOLID_LAYER;

    @JSCodingField(description = "Layer index for transparent 3D models.")
    public static final int MODEL_TRANSPARENCY_LAYER = MeshStructure3D.TRANSPARENCY_LAYER;

    @JSCodingFunctionOrMethod(description = "Returns maximum number of texture units supported by GPU.", paramNames = {})
    public int getMaxTextureUnits() {
        return GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @JSCodingFunctionOrMethod(description = "Renders a 2D model with specific render mode.", paramNames = {"model2D", "renderMode"})
    public void renderModel2D(@NotNull JSModel2D model2D, int renderMode) {
        JGemsHelper.render().renderModel2D(model2D.getJavaModel2D(), renderMode);
    }

    @JSCodingFunctionOrMethod(description = "Renders a 2D model using default GL_TRIANGLES mode.", paramNames = {"model2D"})
    public void renderModel2D(@NotNull JSModel2D model2D) {
        JGemsHelper.render().renderModel2D(model2D.getJavaModel2D(), GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "Renders a 3D model on specified layer.", paramNames = {"model3D", "layer"})
    public void renderModel3D(@NotNull JSModel3D model3D, int layer) {
        JGemsHelper.render().renderModel3D(model3D.getJavaModel3D(), layer, GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "Renders a 3D model only on solid layers.", paramNames = {"model3D"})
    public void renderModel3D_SolidLayers(@NotNull JSModel3D model3D) {
        JGemsHelper.render().renderModel3D(model3D.getJavaModel3D(), MODEL_SOLID_LAYER, GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "Renders a 3D model only on transparent layers.", paramNames = {"model3D"})
    public void renderModel3D_TransparentLayers(@NotNull JSModel3D model3D) {
        JGemsHelper.render().renderModel3D(model3D.getJavaModel3D(), MODEL_TRANSPARENCY_LAYER, GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "Renders a list of 3D mesh nodes.", paramNames = {"list", "code"})
    public void renderMeshList3D(@NotNull List<JSMeshNode3D> list, int code) {
        for (JSMeshNode3D meshNode3D : list) {
            JGemsHelper.render().renderMeshNode((RenderMesh) meshNode3D.getJavaMeshNode3D().getMeshData());
        }
    }

    @JSCodingFunctionOrMethod(description = "Returns texturing code for a given material.", paramNames = {"material"})
    public int getTexturingCodeForShader(@NotNull JSMaterial material) {
        return JGemsHelper.render().getTexturingCodeForShader(material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Performs default material setup on shader.", paramNames = {"environment", "shaderManager", "material"})
    public void performDefaultModelMaterialOnShader(@NotNull JSEnvironment environment, @NotNull JSShader shaderManager, @NotNull JSMaterial material) {
        JGemsHelper.render().performDefaultModelMaterialOnShader(environment.getJavaEnvironment(), shaderManager.getJavaShaderManager(), material.getJavaMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Prepares empty animations info for shader.", paramNames = {"shaderManager"})
    public void performEmptyAnimationsInfo(@NotNull JSShader shaderManager) {
        JGemsHelper.render().performEmptyAnimationsInfo(shaderManager.getJavaShaderManager());
    }

    @JSCodingFunctionOrMethod(description = "Prepares animation info for shader using mesh structure.", paramNames = {"shaderManager", "meshStructure3D"})
    public void performAnimationsInfo(@NotNull JSShader shaderManager, @NotNull JSMeshStructure3D meshStructure3D) {
        JGemsHelper.render().performAnimationsInfo(JGemsHelper.resources().getResourceManager(), shaderManager.getJavaShaderManager(), (IAnimated) meshStructure3D.getJavaMeshStructure3D());
    }

    @JSCodingFunctionOrMethod(description = "Performs shadows info on shader.", paramNames = {"environment", "shaderManager"})
    public void performShadowsInfo(@NotNull JSEnvironment environment, @NotNull JSShader shaderManager) {
        JGemsHelper.render().performShadowsInfo(environment.getJavaEnvironment(), shaderManager.getJavaShaderManager());
    }

    @JSHideFromDoc
    @Override
    public JSRenderHelperClass newGlobalVar() {
        return new JSRenderHelperClass();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "JSRenderHelper";
    }
}