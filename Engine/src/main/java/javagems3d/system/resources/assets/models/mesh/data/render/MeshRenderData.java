/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.assets.models.mesh.data.render;

import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

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

    public MeshRenderData allowMoveMeshesIntoTransparencyPass(boolean allowMoveMeshesIntoTransparencyPass) {
        this.allowMoveMeshesIntoTransparencyPass = allowMoveMeshesIntoTransparencyPass;
        return this;
    }

    public MeshRenderData setMeshRenderAttributes(@NotNull MeshRenderAttributes meshRenderAttributes) {
        this.meshRenderAttributes = meshRenderAttributes;
        return this;
    }

    public Material getOverlappingMaterial() {
        return this.overlappingMaterial;
    }

    public MeshRenderData setOverlappingMaterial(Material overlappingMaterial) {
        this.overlappingMaterial = overlappingMaterial;
        return this;
    }

    public boolean isAllowMoveMeshesIntoTransparencyPass() {
        return this.allowMoveMeshesIntoTransparencyPass;
    }

    public JGemsShaderManager getOverridenTransparencyShader() {
        return this.overridenTransparencyShader;
    }

    public MeshRenderData setOverridenTransparencyShader(JGemsShaderManager overridenTransparencyShader) {
        this.overridenTransparencyShader = overridenTransparencyShader;
        return this;
    }

    public @NotNull MeshRenderAttributes getRenderAttributes() {
        return this.meshRenderAttributes;
    }

    public @NotNull JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public MeshRenderData setShaderManager(@NotNull JGemsShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        return this;
    }
}
