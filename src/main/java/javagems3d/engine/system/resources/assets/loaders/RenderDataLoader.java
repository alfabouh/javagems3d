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

package javagems3d.engine.system.resources.assets.loaders;

import org.joml.Vector3f;
import javagems3d.engine.JGems3D;
import javagems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import javagems3d.engine.graphics.opengl.rendering.fabric.inventory.render.InventoryZippo;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderEntity;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderEntity2D3D;
import javagems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderPlayer;
import javagems3d.engine.graphics.opengl.rendering.items.objects.EntityObject;
import javagems3d.engine.graphics.opengl.rendering.items.objects.PlayerSPObject;
import javagems3d.engine.graphics.opengl.rendering.items.objects.WorldEntity;
import javagems3d.engine.physics.world.basic.WorldItem;
import javagems3d.engine.system.inventory.items.ItemZippo;
import javagems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import javagems3d.engine.system.resources.assets.material.Material;
import javagems3d.engine.system.resources.assets.models.helper.MeshHelper;
import javagems3d.engine.system.resources.assets.models.helper.constructor.IEntityModelConstructor;
import javagems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import javagems3d.engine.system.resources.manager.GameResources;
import javagems3d.engine.system.resources.manager.JGemsResourceManager;

public class RenderDataLoader implements IAssetsLoader {
    public RenderEntityData entityCube;
    public RenderEntityData player;
    public RenderEntityData ground;
    public RenderEntityData zippo_world;
    public RenderLiquidData water;

    public RenderDataLoader() {
    }

    @Override
    public void load(GameResources gameResources) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Building render data...");
        IEntityModelConstructor<WorldItem> itemPickUpModelConstructor = e -> new MeshDataGroup(MeshHelper.generateSimplePlane3DMesh(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0.5f, 0.5f, 0.0f)));

        Material zwMat = new Material();
        zwMat.setDiffuse(JGemsResourceManager.globalTextureAssets.zippo1);
        this.zippo_world = new RenderEntityData(new RenderEntity2D3D(), WorldEntity.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.zippo_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.zippo_world.getMeshRenderData().allowMoveMeshesIntoTransparencyPass(false).getRenderAttributes().setAlphaDiscard(0.6f).setShadowCaster(false).setRenderDistance(64.0f);
        this.zippo_world.getMeshRenderData().setOverlappingMaterial(zwMat);

        this.water = new RenderLiquidData(new Material(JGemsResourceManager.globalTextureAssets.waterTexture).setFullOpacity(0.5f), JGemsResourceManager.globalShaderAssets.weighted_liquid_oit);

        this.entityCube = new RenderEntityData(new RenderEntity(), WorldEntity.class, JGemsResourceManager.globalShaderAssets.world_gbuffer).setMeshDataGroup(JGemsResourceManager.globalModelAssets.cube); //TODO

        this.player = new RenderEntityData(new RenderPlayer(), PlayerSPObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);

        this.ground = new RenderEntityData(new RenderEntity(), EntityObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);
        this.ground.getMeshRenderData().getRenderAttributes().setAlphaDiscard(0.25f);

        JGemsResourceManager.addInventoryItemRenderer(ItemZippo.class, new InventoryItemRenderData(JGemsResourceManager.globalShaderAssets.inventory_common_item, new InventoryZippo(), JGemsResourceManager.globalTextureAssets.zippo1));
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
