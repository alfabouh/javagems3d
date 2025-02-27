package javagems3d.graphics.objects.rendering.data;

import javagems3d.system.resources.assets.materials.Material;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public final class LiquidRenderData {
    private final Material liquidMaterial;
    private final JGemsShaderManager shaderManager;

    public LiquidRenderData(@NotNull Material liquidMaterial, JGemsShaderManager shaderManager) {
        this.liquidMaterial = liquidMaterial;
        this.shaderManager = shaderManager;
    }

    public Material getLiquidMaterial() {
        return this.liquidMaterial;
    }

    public JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }
}