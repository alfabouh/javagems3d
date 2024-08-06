package ru.jgems3d.engine.system.resources.assets.models.mesh.data.render;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.resources.assets.materials.Material;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

@SuppressWarnings("all")
public final class MeshRenderData {
    private JGemsShaderManager shaderManager;
    private MeshRenderAttributes meshRenderAttributes;

    private boolean allowMoveMeshesIntoTransparencyPass;
    private JGemsShaderManager overridenTransparencyShader;
    private Material overlappingMaterial;

    public MeshRenderData(MeshRenderAttributes meshRenderAttributes, @NotNull JGemsShaderManager shaderManager) {
        this.setShaderManager(shaderManager);
        this.meshRenderAttributes = meshRenderAttributes;

        this.overridenTransparencyShader = null;
        this.allowMoveMeshesIntoTransparencyPass = true;
        this.overlappingMaterial = null;
    }

    public static MeshRenderData defaultMeshRenderData(@NotNull JGemsShaderManager shaderManager) {
        return new MeshRenderData(new MeshRenderAttributes(), shaderManager);
    }

    public MeshRenderData copy() {
        MeshRenderData meshRenderData = new MeshRenderData(this.getRenderAttributes().copy(), this.getShaderManager());
        meshRenderData.setOverlappingMaterial(this.getOverlappingMaterial());
        meshRenderData.allowMoveMeshesIntoTransparencyPass(this.isAllowMoveMeshesIntoTransparencyPass());
        meshRenderData.setOverridenTransparencyShader(this.getOverridenTransparencyShader());
        return meshRenderData;
    }

    public MeshRenderData setOverlappingMaterial(Material overlappingMaterial) {
        this.overlappingMaterial = overlappingMaterial;
        return this;
    }

    public MeshRenderData setOverridenTransparencyShader(JGemsShaderManager overridenTransparencyShader) {
        this.overridenTransparencyShader = overridenTransparencyShader;
        return this;
    }

    public MeshRenderData allowMoveMeshesIntoTransparencyPass(boolean allowMoveMeshesIntoTransparencyPass) {
        this.allowMoveMeshesIntoTransparencyPass = allowMoveMeshesIntoTransparencyPass;
        return this;
    }

    public MeshRenderData setMeshRenderAttributes(@NotNull MeshRenderAttributes meshRenderAttributes) {
        this.meshRenderAttributes = meshRenderAttributes;
        return this;
    }

    public MeshRenderData setShaderManager(@NotNull JGemsShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        return this;
    }

    public Material getOverlappingMaterial() {
        return this.overlappingMaterial;
    }

    public boolean isAllowMoveMeshesIntoTransparencyPass() {
        return this.allowMoveMeshesIntoTransparencyPass;
    }

    public JGemsShaderManager getOverridenTransparencyShader() {
        return this.overridenTransparencyShader;
    }

    public @NotNull MeshRenderAttributes getRenderAttributes() {
        return this.meshRenderAttributes;
    }

    public @NotNull JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }
}
