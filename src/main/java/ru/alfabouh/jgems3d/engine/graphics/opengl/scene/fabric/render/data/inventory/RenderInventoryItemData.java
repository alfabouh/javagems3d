package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.inventory;

import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderInventoryItem;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.samples.TextureSample;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class RenderInventoryItemData {
    private final JGemsShaderManager shaderManager;
    private final IRenderInventoryItem renderFabric;
    private final TextureSample inventoryIcon;

    public RenderInventoryItemData(JGemsShaderManager shaderManager, IRenderInventoryItem renderFabric) {
        this(shaderManager, renderFabric, null);
    }

    public RenderInventoryItemData(JGemsShaderManager shaderManager, IRenderInventoryItem renderFabric, TextureSample inventoryIcon) {
        this.shaderManager = shaderManager;
        this.renderFabric = renderFabric;
        this.inventoryIcon = inventoryIcon;
    }

    public TextureSample getInventoryIcon() {
        return this.inventoryIcon;
    }

    public IRenderInventoryItem getRenderFabric() {
        return this.renderFabric;
    }

    public JGemsShaderManager getShaderManager() {
        return this.shaderManager;
    }
}
