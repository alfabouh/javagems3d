package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.ForwardRenderNode;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

public class JGemsForwardRenderNode extends ForwardRenderNode {

    public JGemsForwardRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return JGemsResourceManager.globalShaderAssets.IndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return JGemsResourceManager.globalShaderAssets.PropertiesData;
    }

    @Override
    public boolean renderBackground() {
        return true;
    }

    @Override
    public @NotNull MeshGroup getCube() {
        return JGemsResourceManager.DEFAULT_CUBE_MESHGROUP();
    }

    @Override
    public @NotNull JGemsShaderManager getSkyBoxShader() {
        return JGemsResourceManager.globalShaderAssets.skybox;
    }
}