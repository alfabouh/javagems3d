package ru.alfabouh.jgems3d.engine.system.resources.assets.loaders;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.inventory.items.*;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.Render2D3DObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.objects.RenderObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.objects.RenderPlayerSP;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderLiquidData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.RenderParticleD2Data;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.inventory.InventoryCommonItemObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.inventory.InventoryEmpObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.inventory.InventoryZippoObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.EntityObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.ParticleObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.PlayerSPObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.WorldItemObject;
import ru.alfabouh.jgems3d.engine.system.resources.assets.loaders.base.IAssetsLoader;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.jgems3d.engine.system.resources.manager.objects.GameResources;

import java.util.HashMap;
import java.util.Map;

public class RenderDataLoader implements IAssetsLoader {
    public final InventoryItemRenderTable inventoryItemRenderTable;
    public RenderObjectData entityCube;
    public RenderObjectData player;
    public RenderObjectData ground;
    public RenderObjectData particleFlame;
    public RenderObjectData door1;
    public RenderObjectData enemy;
    public RenderObjectData zippo_world;
    public RenderObjectData radio_world;
    public RenderObjectData emp_world;
    public RenderObjectData crowbar_world;
    public RenderObjectData soda_world;
    public RenderObjectData plank;
    public RenderLiquidData water;

    public RenderObjectData cd_world;
    public RenderObjectData cassette_world;

    public RenderDataLoader() {
        this.inventoryItemRenderTable = new InventoryItemRenderTable();
    }

    @Override
    public void load(GameResources gameResources) {
        JGems.get().getScreen().addLineInLoadingScreen("Loading render data...");
        this.inventoryItemRenderTable.addItem(ItemZippo.class, new RenderInventoryItemData(JGemsResourceManager.globalShaderAssets.inventory_zippo, new InventoryZippoObject(), JGemsResourceManager.renderAssets.zippo_inventory));
        this.inventoryItemRenderTable.addItem(ItemEmp.class, new RenderInventoryItemData(JGemsResourceManager.globalShaderAssets.inventory_common_item, new InventoryEmpObject(), JGemsResourceManager.renderAssets.emp_inventory));
        this.inventoryItemRenderTable.addItem(ItemCrowbar.class, new RenderInventoryItemData(JGemsResourceManager.globalShaderAssets.inventory_common_item, new InventoryCommonItemObject(JGemsResourceManager.renderAssets.crowbar), JGemsResourceManager.renderAssets.crowbar_inventory));
        this.inventoryItemRenderTable.addItem(ItemRadio.class, new RenderInventoryItemData(JGemsResourceManager.globalShaderAssets.inventory_common_item, new InventoryCommonItemObject(JGemsResourceManager.renderAssets.radio), JGemsResourceManager.renderAssets.radio_inventory));

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

        Material enMat = new Material();
        enMat.setDiffuse(JGemsResourceManager.renderAssets.enemyTexture);

        Material zwMat = new Material();
        zwMat.setDiffuse(JGemsResourceManager.renderAssets.zippo_world);

        Material cwMat = new Material();
        cwMat.setDiffuse(JGemsResourceManager.renderAssets.crowbar_world);

        Material ewMat = new Material();
        ewMat.setDiffuse(JGemsResourceManager.renderAssets.emp_world);

        Material rdMat = new Material();
        rdMat.setDiffuse(JGemsResourceManager.renderAssets.radio_world);

        Material sdMat = new Material();
        sdMat.setDiffuse(JGemsResourceManager.renderAssets.soda_world);

        Material cdMat = new Material();
        cdMat.setDiffuse(JGemsResourceManager.renderAssets.cd_world);

        Material csMat = new Material();
        csMat.setDiffuse(JGemsResourceManager.renderAssets.cassette_world);

        this.water = new RenderLiquidData(JGemsResourceManager.renderAssets.waterNormals, JGemsResourceManager.renderAssets.waterTexture, false, JGemsResourceManager.globalShaderAssets.world_liquid_gbuffer);

        this.enemy = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_enemy);
        this.enemy.setEntityModelConstructor(enemyModelConstructor);
        this.enemy.getModelRenderParams().setShadowCaster(false).setHasTransparency(true);
        this.enemy.setOverObjectMaterial(enMat);

        this.soda_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.soda_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.soda_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.soda_world.setOverObjectMaterial(sdMat);

        this.radio_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.radio_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.radio_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.radio_world.setOverObjectMaterial(rdMat);

        this.zippo_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.zippo_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.zippo_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.zippo_world.setOverObjectMaterial(zwMat);

        this.emp_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.emp_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.emp_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.emp_world.setOverObjectMaterial(ewMat);

        this.crowbar_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.crowbar_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.crowbar_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.crowbar_world.setOverObjectMaterial(cwMat);

        this.cd_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.cd_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.cd_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.cd_world.setOverObjectMaterial(cdMat);

        this.cassette_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_pickable);
        this.cassette_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.cassette_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.cassette_world.setOverObjectMaterial(csMat);

        this.plank = new RenderObjectData(new RenderObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer).setMeshDataGroup(JGemsResourceManager.modelAssets.plank);

        this.door1 = new RenderObjectData(new RenderObject(), WorldItemObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer).setMeshDataGroup(JGemsResourceManager.modelAssets.door2);
        this.door1.getModelRenderParams().setShouldInterpolateMovement(false);

        this.entityCube = new RenderObjectData(new RenderObject(), EntityObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer).setMeshDataGroup(JGemsResourceManager.modelAssets.cube);
        this.player = new RenderObjectData(new RenderPlayerSP(), PlayerSPObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);

        this.ground = new RenderObjectData(new RenderObject(), EntityObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer);
        this.ground.getModelRenderParams().setAlphaDiscard(0.25f);

        this.particleFlame = new RenderParticleD2Data(new RenderObject(), ParticleObject.class, JGemsResourceManager.globalShaderAssets.world_gbuffer, JGemsResourceManager.renderAssets.particleTexturePack, true);

        JGems.get().getScreen().addLineInLoadingScreen("Render data successfully loaded...");
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.POST;
    }

    @Override
    public int loadOrder() {
        return 4;
    }

    public static class InventoryItemRenderTable {
        private final Map<Class<? extends InventoryItem>, RenderInventoryItemData> map;

        public InventoryItemRenderTable() {
            this.map = new HashMap<>();
        }

        public boolean hasRender(InventoryItem inventoryItem) {
            return this.getMap().containsKey(inventoryItem.getClass());
        }

        public void addItem(Class<? extends InventoryItem> clazz, RenderInventoryItemData renderInventoryItemData) {
            this.getMap().put(clazz, renderInventoryItemData);
        }

        public Map<Class<? extends InventoryItem>, RenderInventoryItemData> getMap() {
            return this.map;
        }
    }
}
