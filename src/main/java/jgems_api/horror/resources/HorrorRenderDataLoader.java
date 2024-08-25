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

package jgems_api.horror.resources;

import jgems_api.horror.inventory.ZippoHorror;
import jgems_api.horror.items.ItemZippoModded;
import jgems_api.horror.render.RenderHorrorPlayer;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.PlayerSPObject;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;


public class HorrorRenderDataLoader implements IAssetsLoader {
    public RenderEntityData player;

    @Override
    public void load(GameResources gameResources) {
        JGemsResourceManager.addInventoryItemRenderer(ItemZippoModded.class, new InventoryItemRenderData(JGemsResourceManager.globalShaderAssets.inventory_zippo, new ZippoHorror(), JGemsResourceManager.globalTextureAssets.zippo_inventory));
        this.player = new RenderEntityData(new RenderHorrorPlayer(), PlayerSPObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.NORMAL;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}

