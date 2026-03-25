package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.ForwardRenderNode;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL46;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.map.MapEditorInterface;
import workbench.graphics.scene.ui.map.editor.utils.GlobalWBenchSceneRenderingVars;
import workbench.graphics.scene.world.WBenchWorld;
import workbench.resources.WBenchResourceManager;

public class WBenchForwardRenderNode extends ForwardRenderNode {
    public WBenchForwardRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        super.onRender(frameTicking);

        if (GlobalWBenchSceneRenderingVars.VIEW_CHESS_TERRAIN) {
            final WBenchWorld wBenchWorld = (WBenchWorld) this.getWorld();
            this.getOutColorBuffer().bindFBO();
            GL46.glEnable(GL46.GL_BLEND);
            GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
            WBenchResourceManager.localShaderAssets.simple_flat.beginShading();
            WBenchResourceManager.localShaderAssets.simple_flat.performMatrix4(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
            WBenchResourceManager.localShaderAssets.simple_flat.performMatrix4(new UniformString(DefaultUniformDefinitions.MODEL_MATRIX), TransformUtils.getModelMatrix(WBenchOpenGLRenderer.flatTerrain.getPose()));
            WBenchResourceManager.localShaderAssets.simple_flat.performMatrix4(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), JGemsTransformManager.INSTANCE.getCameraViewMatrix());
            if (WBenchOpenGLRenderer.isRenderingBackgroundScene()) {
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.65f, 0.35f, 0.5f)));
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString(DefaultUniformDefinitions.DRAW_CENTER_RECT), UniformFunctions.FLOAT(128.0f / wBenchWorld.getEnvironment().getSkyBox().getBackground().getViewScaling()));
            } else {
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString(DefaultUniformDefinitions.COLOR), UniformFunctions.VEC4F(new Vector4f(0.35f, 0.35f, 0.65f, 0.5f)));
                WBenchResourceManager.localShaderAssets.simple_flat.performUniform(new UniformString(DefaultUniformDefinitions.DRAW_CENTER_RECT), UniformFunctions.FLOAT(-1.0f));
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
        return WBenchResourceManager.DEFAULT_CUBE_MESHGROUP();
    }

    @Override
    public @NotNull JGemsShaderManager getSkyBoxShader() {
        return WBenchResourceManager.localShaderAssets.skybox;
    }
}
