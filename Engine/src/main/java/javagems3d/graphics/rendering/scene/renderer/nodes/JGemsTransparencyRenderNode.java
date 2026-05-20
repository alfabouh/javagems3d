package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.objects.entities.world.SceneWorldLiquid;
import javagems3d.graphics.objects.rendering.pipeline.fabric.scene.DefaultDirectRenderFabric;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.TransparencyRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class JGemsTransparencyRenderNode extends TransparencyRenderNode {
    private Collection<SceneWorldLiquid> worldLiquid;

    public JGemsTransparencyRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
        this.worldLiquid = new HashSet<>();
    }

    @Override
    protected void renderContent(FrameTicking frameTicking) {
        super.renderContent(frameTicking);

        for (SceneWorldLiquid sceneWorldLiquid : this.worldLiquid) {
            this.renderLiquid(sceneWorldLiquid);
        }
    }

    protected void renderLiquid(SceneWorldLiquid sceneWorldLiquid) {
        JGemsShaderManager shaderManager = sceneWorldLiquid.getRenderLiquidData().shaderManager();
        shaderManager.beginShading();
        shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
        shaderManager.performModel3DMatrix(new UniformString(DefaultUniformDefinitions.MODEL_MATRIX), sceneWorldLiquid.getModel());
        shaderManager.performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
        shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.TEXTURE_SCALING), UniformFunctions.VEC2F(sceneWorldLiquid.getTextureScaling()));
        shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.CAMERA_POS), UniformFunctions.VEC3F(this.getOpenGLRenderer().getCamera().getCamPosition()));
        this.renderMeshList3D(this.getOpenGLRenderer(), shaderManager, sceneWorldLiquid.getModel(), MeshStructure3D.TRANSPARENCY_LAYER);
        shaderManager.endShading();
    }

    public void renderMeshList3D(OpenGLRenderer openGLRenderer, JGemsShaderManager shaderManager, Model3D model3D, int layer) {
        for (MeshNode3D<RenderMesh> meshNode3D : DefaultDirectRenderFabric.getNodes(model3D.<MeshStructure3D<RenderMesh>>getMeshStructureCast().getNodes(layer), model3D.getPose(), openGLRenderer, model3D.getMeshStructureCast())) {
            JGemsHelper.render().performDefaultModelMaterialOnShader(openGLRenderer.getWorld().getEnvironment(), shaderManager, meshNode3D.getMaterial(), 1.0f, 0);
            JGemsHelper.render().performShadowsInfo(openGLRenderer.getWorld().getEnvironment(), shaderManager);
            JGemsHelper.render().renderMeshNode(meshNode3D.getMeshData());
        }
    }

    public JGemsTransparencyRenderNode setWorldLiquid(Collection<SceneWorldLiquid> worldLiquid) {
        this.worldLiquid = worldLiquid;
        return this;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return JGemsResourceManager.globalShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return JGemsResourceManager.globalShaderAssets.MainScenePropertiesData;
    }
}
