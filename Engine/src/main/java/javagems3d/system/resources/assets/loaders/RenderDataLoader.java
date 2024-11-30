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

package javagems3d.system.resources.assets.loaders;

import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import javagems3d.graphics.opengl.rendering.fabric.inventory.render.InventoryZippo;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import javagems3d.graphics.OLD.fabric.objects.render.RenderEntity;
import javagems3d.graphics.OLD.fabric.objects.render.RenderEntity2D3D;
import javagems3d.graphics.OLD.fabric.objects.render.RenderPlayer;
import javagems3d.graphics.objects.entities.EntityObject;
import javagems3d.graphics.objects.entities.PlayerSPObject;
import javagems3d.graphics.objects.entities.WorldEntity;
import javagems3d.physics.world.basic.WorldItem;
import javagems3d.system.inventory.items.ItemZippo;
import javagems3d.system.resources.assets.loaders.base.IAssetsLoader;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import javagems3d.system.resources.manager.GameResources;
import javagems3d.system.resources.manager.JGemsResourceManager;

public class RenderDataLoader implements IAssetsLoader {
    public EntityRenderData entityCube;
    public EntityRenderData player;
    public EntityRenderData ground;
    public EntityRenderData zippo_world;
    public LiquidRenderData water;

    public RenderDataLoader() {
    }

    @Override
    public void load(GameResources gameResources) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Building render data...");
        IEntityModelConstructor<WorldItem> itemPickUpModelConstructor = e -> {
            MeshGroup meshGroup = new MeshGroup(new MeshGroup.MeshGroupNode(MeshHelper.generateSimplePlane3DMesh(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0.5f, 0.5f, 0.0f))));
            return meshGroup;
        };

        Material zwMat = new Material();
        zwMat.setDiffuse(JGemsResourceManager.globalTextureAssets.zippo1);
        this.zippo_world = new EntityRenderData(new RenderEntity2D3D(), WorldEntity.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.zippo_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.zippo_world.getObjectRenderSettings().setAllowMoveMeshesIntoTransparencyPass(false).setAlphaDiscardValue(0.6f).setShadowCaster(false).setRenderDistance(64.0f);
        //this.zippo_world.getObjectRenderSettings().setOverlappingMaterial(zwMat);

        this.water = new LiquidRenderData(new Material(JGemsResourceManager.globalTextureAssets.waterTexture).setFullOpacity(0.5f), JGemsResourceManager.globalShaderAssets.weighted_liquid_oit);

        this.entityCube = new EntityRenderData(new RenderEntity(), WorldEntity.class, JGemsResourceManager.globalShaderAssets.world_gbuffer).setMeshDataGroup(JGemsResourceManager.globalModelAssets.cube); //TODO

        this.player = new EntityRenderData(new RenderPlayer(), PlayerSPObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);

        this.ground = new EntityRenderData(new RenderEntity(), EntityObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);
        this.ground.getObjectRenderSettings().setAlphaDiscardValue(0.25f);

        JGemsResourceManager.addInventoryItemRenderer(ItemZippo.class, new InventoryItemRenderData(JGemsResourceManager.globalShaderAssets.inventory_common_item, new InventoryZippo(), JGemsResourceManager.globalTextureAssets.zippo1));
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }

}
