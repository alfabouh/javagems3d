package ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data;

import org.jetbrains.annotations.NotNull;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public final class RenderLiquidData {
    private final Material liquidMaterial;
    private final JGemsShaderManager shaderManager;

    public RenderLiquidData(@NotNull Material liquidMaterial, JGemsShaderManager shaderManager) {
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