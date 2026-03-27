package api.scripting.coding.env.internal.util.screen.render;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.resources.instances.models.JSMaterial;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel2D;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMeshStructure3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.nodes.JSMeshNode3D;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.List;

@JSCodingClass(binding = "JSRenderHelperClass", description = "...")
public class JSRenderHelperClass implements JSGlobalVarFactory<JSRenderHelperClass> {
    @JSCodingField(description = "...") public static final int DIFFUSE_CODE = 1 << 2;
    @JSCodingField(description = "...") public static final int NORMALS_CODE = 1 << 3;
    @JSCodingField(description = "...") public static final int EMISSION_CODE = 1 << 4;
    @JSCodingField(description = "...") public static final int METALLIC_ROUGHNESS_CODE = 1 << 5;

    @JSCodingField(description = "...") public static final int MODEL_SOLID_LAYER = MeshStructure3D.SOLID_LAYER;
    @JSCodingField(description = "...") public static final int MODEL_TRANSPARENCY_LAYER = MeshStructure3D.TRANSPARENCY_LAYER;

    @JSCodingFunctionOrMethod(description = "...")
    public int getMaxTextureUnits() {
        return GL46.glGetInteger(GL46.GL_MAX_TEXTURE_IMAGE_UNITS);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderModel2D(JSModel2D model2D, int renderMode) {
        JGemsHelper.render().renderModel2D(model2D.getJavaModel2D(), renderMode);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderModel2D(JSModel2D model2D) {
        JGemsHelper.render().renderModel2D(model2D.getJavaModel2D(), GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderModel3D(JSModel3D model2D, int layer) {
        JGemsHelper.render().renderModel3D(model2D.getJavaModel3D(), layer, GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderModel3D_SolidLayers(JSModel3D model2D) {
        JGemsHelper.render().renderModel3D(model2D.getJavaModel3D(), JSRenderHelperClass.MODEL_SOLID_LAYER, GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderModel3D_TransparentLayers(JSModel3D model2D) {
        JGemsHelper.render().renderModel3D(model2D.getJavaModel3D(), JSRenderHelperClass.MODEL_TRANSPARENCY_LAYER, GL46.GL_TRIANGLES);
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void renderMeshList3D(List<JSMeshNode3D> list, int code) {
        for (JSMeshNode3D meshNode3D : list) {
            JGemsHelper.render().renderMeshNode((RenderMesh) meshNode3D.getJavaMeshNode3D().getMeshData());
        }
    }

    @JSCodingFunctionOrMethod(description = "...")
    public int getTexturingCodeForShader(JSMaterial material) {
        return JGemsHelper.render().getTexturingCodeForShader(material.getJavaMaterial());
    }

    //TODO
    public void performDefaultModelMaterialOnShader(IEnvironment environment, JGemsShaderManager shaderManager, Material material) {

    }

    @JSCodingFunctionOrMethod(description = "...")
    public void performEmptyAnimationsInfo(@NotNull JSShader shaderManager) {
        JGemsHelper.render().performEmptyAnimationsInfo(shaderManager.getJavaShaderManager());
    }

    //TODO
    @JSCodingFunctionOrMethod(description = "...")
    public void performAnimationsInfo(@NotNull JSShader shaderManager, @NotNull JSMeshStructure3D meshStructure3D) {
        //JGemsHelper.render().performAnimationsInfo((ResourceManager) JGemsHelper.resources().getResourceManager(), shaderManager.getJavaShaderManager(), (IAnimated) meshStructure3D.getJavaMeshStructure3D());
    }

    //TODO
    public void performShadowsInfo(IEnvironment environment, JGemsShaderManager shaderManager) {

    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {""})
    public void fun() {
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
