package ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data;

import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.IRenderInventoryFabric;
import ru.jgems3d.engine.system.resources.assets.material.samples.TextureSample;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

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
