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

package javagems3d.graphics.opengl.rendering.fabric.inventory.data;

import javagems3d.graphics.opengl.rendering.fabric.inventory.IRenderInventoryFabric;
import javagems3d.system.resources.assets.material.samples.TextureSample;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public class InventoryItemRenderData {
    private final JGemsShaderManager shaderManager;
    private final IRenderInventoryFabric renderFabric;
    private final TextureSample inventoryIcon;

    public InventoryItemRenderData(JGemsShaderManager shaderManager, IRenderInventoryFabric renderFabric) {
        this(shaderManager, renderFabric, null);
    }

    public InventoryItemRenderData(JGemsShaderManager shaderManager, IRenderInventoryFabric renderFabric, TextureSample inventoryIcon) {
        this.shaderManager = shaderManager;
        this.renderFabric = renderFabric;
        this.inventoryIcon = inventoryIcon;
    }

    public TextureSample getInventoryIcon() {
        return this.inventoryIcon;
    }

    public IRenderInventoryFabric getRenderFabric() {
        return this.renderFabric;
    }

    public JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }
}
