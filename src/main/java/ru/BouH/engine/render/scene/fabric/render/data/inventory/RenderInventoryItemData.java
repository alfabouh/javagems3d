package ru.BouH.engine.render.scene.fabric.render.data.inventory;

import ru.BouH.engine.game.resources.assets.materials.textures.TextureSample;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.fabric.render.base.IRenderInventoryItem;

public class RenderInventoryItemData {
    private final ShaderManager shaderManager;
    private final IRenderInventoryItem renderFabric;
    private final TextureSample inventoryIcon;

    public RenderInventoryItemData(ShaderManager shaderManager, IRenderInventoryItem renderFabric) {
        this(shaderManager, renderFabric, null);
    }

    public RenderInventoryItemData(ShaderManager shaderManager, IRenderInventoryItem renderFabric, TextureSample inventoryIcon) {
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

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }
}
