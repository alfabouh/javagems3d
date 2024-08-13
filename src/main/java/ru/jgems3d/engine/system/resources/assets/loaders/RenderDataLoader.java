package ru.jgems3d.engine.system.resources.assets.loaders;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.data.InventoryItemRenderData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.inventory.render.AbstractInventoryZippo;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderEntity2D3D;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.render.RenderPlayer;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.WorldEntity;
import ru.jgems3d.engine.system.inventory.items.ItemZippo;
import ru.jgems3d.engine.physics.world.basic.WorldItem;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderLiquidData;
import ru.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.RenderEntityData;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.EntityObject;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.PlayerSPObject;
import ru.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.jgems3d.engine.system.resources.assets.material.Material;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.manager.GameResources;

public class RenderDataLoader implements IAssetsLoader {
    public RenderEntityData entityCube;
    public RenderEntityData player;
    public RenderEntityData ground;
    public RenderEntityData particleFlame;
    public RenderEntityData zippo_world;
    public RenderLiquidData water;

    public RenderDataLoader() {
    }

    @Override
    public void load(GameResources gameResources) {
        JGems3D.get().getScreen().tryAddLineInLoadingScreen(0x00ff00, "Loading render collections...");

        //IEntityModelConstructor<WorldItem> entityModelConstructor = e -> {
        //    Plane4dBrush plane4dBrush = (Plane4dBrush) e;
        //    return new MeshDataGroup(MeshHelper.generatePlane3DMesh(MathHelper.convertV3DV3F(plane4dBrush.getVertices()[0]), MathHelper.convertV3DV3F(plane4dBrush.getVertices()[1]), MathHelper.convertV3DV3F(plane4dBrush.getVertices()[2]), MathHelper.convertV3DV3F(plane4dBrush.getVertices()[3])));
        //};
        IEntityModelConstructor<WorldItem> enemyModelConstructor = e -> {
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(new Vector3f(-1.0f, 0.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(-1.0f, 2.0f, 0.0f), new Vector3f(1.0f, 2.0f, 0.0f)));
        };
        IEntityModelConstructor<WorldItem> itemPickUpModelConstructor = e -> {
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0.5f, 0.5f, 0.0f)));
        };

        Material zwMat = new Material();
        zwMat.setDiffuse(JGemsResourceManager.globalTextureAssets.zippo_world);

        this.water = new RenderLiquidData(new Material(JGemsResourceManager.globalTextureAssets.waterTexture).setFullOpacity(0.5f), JGemsResourceManager.globalShaderAssets.weighted_liquid_oit);

        this.zippo_world = new RenderEntityData(new RenderEntity2D3D(), WorldEntity.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.zippo_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.zippo_world.getMeshRenderData().getRenderAttributes().setShadowCaster(false).setRenderDistance(64.0f);
        this.zippo_world.getMeshRenderData().setOverlappingMaterial(zwMat);

        this.entityCube = new RenderEntityData(new RenderEntity(), WorldEntity.class, JGemsResourceManager.globalShaderAssets.world_gbuffer).setMeshDataGroup(JGemsResourceManager.globalModelAssets.cube); //TODO

        this.player = new RenderEntityData(new RenderPlayer(), PlayerSPObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);

        this.ground = new RenderEntityData(new RenderEntity(), EntityObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);
        this.ground.getMeshRenderData().getRenderAttributes().setAlphaDiscard(0.25f);

        JGemsResourceManager.addInventoryItemRenderer(ItemZippo.class, new InventoryItemRenderData(JGemsResourceManager.globalShaderAssets.inventory_zippo, new AbstractInventoryZippo(), JGemsResourceManager.globalTextureAssets.zippo_inventory));
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
