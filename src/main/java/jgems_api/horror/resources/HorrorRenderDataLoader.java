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

import jgems_api.horror.HorrorGame;
import jgems_api.horror.inventory.CommonItemHorror;
import jgems_api.horror.inventory.ZippoHorror;
import jgems_api.horror.items.ItemCross;
import jgems_api.horror.items.ItemZippoModded;
import jgems_api.horror.render.RenderHorrorPlayer;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.render.InventoryCommon;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderEntity2D3D;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.PlayerSPObject;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.WorldEntity;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.models.helper.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.manager.GameResources;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;


public class HorrorRenderDataLoader implements IAssetsLoader {
    public RenderEntityData player;
    public RenderEntityData gas_world;
    public RenderEntityData cross_world;

    @Override
    public void load(GameResources gameResources) {
        this.player = new RenderEntityData(new RenderHorrorPlayer(), PlayerSPObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);

        IEntityModelConstructor<WorldItem> itemPickUpModelConstructor = e -> new MeshDataGroup(MeshHelper.generateSimplePlane3DMesh(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0.5f, 0.5f, 0.0f)));

        Material mat1 = new Material();
        mat1.setDiffuse(HorrorGame.get().horrorTexturesLoader.gas);
        this.gas_world = new RenderEntityData(new RenderEntity2D3D(), WorldEntity.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.gas_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.gas_world.getMeshRenderData().allowMoveMeshesIntoTransparencyPass(false).getRenderAttributes().setAlphaDiscard(0.6f).setShadowCaster(false).setRenderDistance(64.0f);
        this.gas_world.getMeshRenderData().setOverlappingMaterial(mat1);

        Material mat2 = new Material();
        mat2.setDiffuse(HorrorGame.get().horrorTexturesLoader.cross);
        this.cross_world = new RenderEntityData(new RenderEntity2D3D(), WorldEntity.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.cross_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.cross_world.getMeshRenderData().allowMoveMeshesIntoTransparencyPass(false).getRenderAttributes().setAlphaDiscard(0.6f).setShadowCaster(false).setRenderDistance(64.0f);
        this.cross_world.getMeshRenderData().setOverlappingMaterial(mat2);

        JGemsResourceManager.addInventoryItemRenderer(ItemZippoModded.class, new InventoryItemRenderData(JGemsResourceManager.globalShaderAssets.inventory_common_item, new ZippoHorror(), JGemsResourceManager.globalTextureAssets.zippo1));
        JGemsResourceManager.addInventoryItemRenderer(ItemCross.class, new InventoryItemRenderData(JGemsResourceManager.globalShaderAssets.inventory_common_item, new CommonItemHorror(HorrorGame.get().horrorTexturesLoader.cross), HorrorGame.get().horrorTexturesLoader.cross));
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

