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

package javagems3d.graphics.opengl.rendering.fabric.inventory.table;

import javagems3d.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import javagems3d.system.inventory.items.InventoryItem;

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
