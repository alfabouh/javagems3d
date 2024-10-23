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

package javagems3d.system.resources.assets.models.properties;

import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

@SuppressWarnings("all")
public final class ModelRenderData {
    private JGemsShaderManager shaderManager;
    private ModelRenderProperties modelRenderProperties;

    private boolean allowMoveMeshesIntoTransparencyPass;
    private JGemsShaderManager overridenTransparencyShader;
    private Material overlappingMaterial;

    public ModelRenderData(ModelRenderProperties modelRenderProperties, @NotNull JGemsShaderManager shaderManager) {
        this.setShaderManager(shaderManager);
        this.modelRenderProperties = modelRenderProperties;

        this.overridenTransparencyShader = null;
        this.allowMoveMeshesIntoTransparencyPass = true;
        this.overlappingMaterial = null;
    }

    public static ModelRenderData defaultMeshRenderData(@NotNull JGemsShaderManager shaderManager) {
        return new ModelRenderData(new ModelRenderProperties(), shaderManager);
    }

    public ModelRenderData copy() {
        ModelRenderData modelRenderData = new ModelRenderData(this.getRenderAttributes().copy(), this.getShaderManager());
        modelRenderData.setOverlappingMaterial(this.getOverlappingMaterial());
        modelRenderData.allowMoveMeshesIntoTransparencyPass(this.isAllowMoveMeshesIntoTransparencyPass());
        modelRenderData.setOverridenTransparencyShader(this.getOverridenTransparencyShader());
        return modelRenderData;
    }

    public ModelRenderData allowMoveMeshesIntoTransparencyPass(boolean allowMoveMeshesIntoTransparencyPass) {
        this.allowMoveMeshesIntoTransparencyPass = allowMoveMeshesIntoTransparencyPass;
        return this;
    }

    public ModelRenderData setMeshRenderAttributes(@NotNull ModelRenderProperties modelRenderProperties) {
        this.modelRenderProperties = modelRenderProperties;
        return this;
    }

    public Material getOverlappingMaterial() {
        return this.overlappingMaterial;
    }

    public ModelRenderData setOverlappingMaterial(Material overlappingMaterial) {
        this.overlappingMaterial = overlappingMaterial;
        return this;
    }

    public boolean isAllowMoveMeshesIntoTransparencyPass() {
        return this.allowMoveMeshesIntoTransparencyPass;
    }

    public JGemsShaderManager getOverridenTransparencyShader() {
        return this.overridenTransparencyShader;
    }

    public ModelRenderData setOverridenTransparencyShader(JGemsShaderManager overridenTransparencyShader) {
        this.overridenTransparencyShader = overridenTransparencyShader;
        return this;
    }

    public @NotNull ModelRenderProperties getRenderAttributes() {
        return this.modelRenderProperties;
    }

    public @NotNull JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public ModelRenderData setShaderManager(@NotNull JGemsShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        return this;
    }
}
