package ru.jgems3d.engine.system.resources.assets.models.mesh.data.render;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

@SuppressWarnings("all")
public final class MeshRenderData {
    private JGemsShaderManager shaderManager;
    private MeshRenderAttributes meshRenderAttributes;

    public MeshRenderData(MeshRenderAttributes meshRenderAttributes, @NotNull JGemsShaderManager shaderManager) {
        this.setShaderManager(shaderManager);
        this.meshRenderAttributes = meshRenderAttributes;
    }

    public static MeshRenderData defaultModelRenderConstraints(@NotNull JGemsShaderManager shaderManager) {
        return new MeshRenderData(new MeshRenderAttributes(), shaderManager);
    }

    public @NotNull MeshRenderAttributes getRenderAttributes() {
        return this.meshRenderAttributes;
    }

    public MeshRenderData setMeshRenderAttributes(@NotNull MeshRenderAttributes meshRenderAttributes) {
        this.meshRenderAttributes = meshRenderAttributes;
        return this;
    }

    @NotNull
    public JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public MeshRenderData setShaderManager(@NotNull JGemsShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        return this;
    }

    public MeshRenderData copy() {
        return new MeshRenderData(this.getRenderAttributes().copy(), this.getShaderManager());
    }
}
