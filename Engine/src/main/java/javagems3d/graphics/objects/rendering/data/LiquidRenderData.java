package javagems3d.graphics.objects.rendering.data;

import javagems3d.system.resources.assets.materials.Material;
import org.jetbrains.annotations.NotNull;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public record LiquidRenderData(Material liquidMaterial, JGemsShaderManager shaderManager) {
    public LiquidRenderData(@NotNull Material liquidMaterial, JGemsShaderManager shaderManager) {
        this.liquidMaterial = liquidMaterial;
        this.shaderManager = shaderManager;
    }
}