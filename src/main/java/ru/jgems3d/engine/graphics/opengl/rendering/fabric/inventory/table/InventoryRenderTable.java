package ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.table;

import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.inventory.items.InventoryItem;

import java.util.HashMap;
import java.util.Map;

public final class InventoryRenderTable {
    private final Map<Class<? extends InventoryItem>, InventoryItemRenderData> map;

    public InventoryRenderTable() {
        this.map = new HashMap<>();
    }

    public boolean hasRender(InventoryItem inventoryItem) {
        return this.getMap().containsKey(inventoryItem.getClass());
    }

    public void addItem(Class<? extends InventoryItem> clazz, InventoryItemRenderData inventoryItemRenderData) {
        this.getMap().put(clazz, inventoryItemRenderData);
    }

    public Map<Class<? extends InventoryItem>, InventoryItemRenderData> getMap() {
        return this.map;
    }
}
