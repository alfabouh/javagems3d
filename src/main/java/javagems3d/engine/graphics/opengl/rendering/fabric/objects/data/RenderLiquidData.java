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

package javagems3d.engine.graphics.opengl.rendering.fabric.objects.data;

import org.jetbrains.annotations.NotNull;
import javagems3d.engine.system.resources.assets.material.Material;
import javagems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

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