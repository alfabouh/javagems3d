package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.ForwardRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

public class WBenchForwardRenderNode extends ForwardRenderNode {
    public WBenchForwardRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        super.onRender(frameTicking);

        if (MapEditorInterface.VIEW_CHESS_TERRAIN) {
            final WBenchWorld wBenchWorld = (WBenchWorld) this.getWorld();
            this.getOutColorBuffer().bindFBO();
            GL46.glEnable(GL46.GL_BLEND);
            GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
            WBenchResourceManager.localShaderAssets.simple_flat.beginShading();
            WBenchResourceManager.localShaderAssets.simple_flat.performPerspectiveMatrix(new UniformString("projection_matrix"), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
            WBenchResourceManager.localShaderAssets.simple_flat.performModel3DMatrix(new UniformString("model_matrix"), TransformUtils.getModelMatrix(WBenchOpenGLRenderer.flatTerrain.getPose()));
            WBenchResourceManager.localShaderAssets.simple_flat.performViewMatrix(new UniformString("view_matrix"), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
            if (WBenchOpenGLRenderer.isRenderingBackgroundScene()) {
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("color"), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.65f, 0.35f, 0.5f)));
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("drawCenterRect"), UniformFunctions.FLOAT(128.0f / wBenchWorld.getEnvironment().getSkyBox().getBackground().getViewScaling()));
            } else {
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("color"), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.35f, 0.65f, 0.5f)));
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString("drawCenterRect"), UniformFunctions.FLOAT(-1.0f));
            }
            JGemsHelper.render().renderModel3D(WBenchOpenGLRenderer.flatTerrain, MeshStructure3D.SOLID_LAYER, GL46.GL_TRIANGLES);
            WBenchResourceManager.localShaderAssets.simple_flat.endShading();
            this.getOutColorBuffer().unBindFBO();
            GL46.glDisable(GL46.GL_BLEND);
            this.getOutColorBuffer().unBindFBO();
        }
    }

    @Override
    public void createResources() {
        super.createResources();
    }

    @Override
    public void destroyResources() {
        super.destroyResources();
    }

    @Override
    public boolean renderBackground() {
        return ((MapEditorInterface) WBenchOpenGLRenderer.getMapEditorInterface()).getOldCamera() == null;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return WBenchResourceManager.localShaderAssets.IndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return WBenchResourceManager.localShaderAssets.PropertiesData;
    }

    @Override
    public @NotNull MeshGroup getCube() {
        return WBenchResourceManager.localModelAssets.defaultCube_gr;
    }

    @Override
    public @NotNull JGemsShaderManager getSkyBoxShader() {
        return WBenchResourceManager.localShaderAssets.skybox;
    }
}
