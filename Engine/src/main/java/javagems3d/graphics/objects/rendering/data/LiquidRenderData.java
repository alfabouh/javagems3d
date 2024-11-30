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

package javagems3d.graphics.objects.rendering.data;

import javagems3d.system.resources.assets.material.Material;
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