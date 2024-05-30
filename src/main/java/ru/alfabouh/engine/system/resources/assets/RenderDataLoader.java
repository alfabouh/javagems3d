package ru.alfabouh.engine.system.resources.assets;

import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.alfabouh.engine.JGems;
import ru.alfabouh.engine.system.resources.ResourceManager;
import ru.alfabouh.engine.system.resources.assets.materials.Material;
import ru.alfabouh.engine.system.resources.assets.models.basic.MeshHelper;
import ru.alfabouh.engine.system.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.alfabouh.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.alfabouh.engine.system.resources.cache.GameCache;
import ru.alfabouh.engine.inventory.items.*;
import ru.alfabouh.engine.math.MathHelper;
import ru.alfabouh.engine.physics.brush.Plane4dBrush;
import ru.alfabouh.engine.physics.world.object.WorldItem;
import ru.alfabouh.engine.render.scene.fabric.Render2D3DObject;
import ru.alfabouh.engine.render.scene.fabric.render.RenderObject;
import ru.alfabouh.engine.render.scene.fabric.render.RenderPlayerSP;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderLiquidData;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.alfabouh.engine.render.scene.fabric.render.data.RenderParticleD2Data;
import ru.alfabouh.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.alfabouh.engine.render.scene.fabric.render.inventory.InventoryCommonItemObject;
import ru.alfabouh.engine.render.scene.fabric.render.inventory.InventoryEmpObject;
import ru.alfabouh.engine.render.scene.fabric.render.inventory.InventoryZippoObject;
import ru.alfabouh.engine.render.scene.objects.items.*;

import java.util.HashMap;
import java.util.Map;

public class RenderDataLoader implements IAssetsLoader {
    public final InventoryItemRenderTable inventoryItemRenderTable;
    public RenderObjectData entityCube;
    public RenderObjectData entityLamp;
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
    public void load(GameCache gameCache) {
        JGems.get().getScreen().addLineInLoadingScreen("Loading render data...");
        this.inventoryItemRenderTable.addItem(ItemZippo.class, new RenderInventoryItemData(ResourceManager.shaderAssets.inventory_zippo, new InventoryZippoObject(), ResourceManager.renderAssets.zippo_inventory));
        this.inventoryItemRenderTable.addItem(ItemEmp.class, new RenderInventoryItemData(ResourceManager.shaderAssets.inventory_common_item, new InventoryEmpObject(), ResourceManager.renderAssets.emp_inventory));
        this.inventoryItemRenderTable.addItem(ItemCrowbar.class, new RenderInventoryItemData(ResourceManager.shaderAssets.inventory_common_item, new InventoryCommonItemObject(ResourceManager.renderAssets.crowbar), ResourceManager.renderAssets.crowbar_inventory));
        this.inventoryItemRenderTable.addItem(ItemRadio.class, new RenderInventoryItemData(ResourceManager.shaderAssets.inventory_common_item, new InventoryCommonItemObject(ResourceManager.renderAssets.radio), ResourceManager.renderAssets.radio_inventory));

        IEntityModelConstructor<WorldItem> entityModelConstructor = e -> {
            Plane4dBrush plane4dBrush = (Plane4dBrush) e;
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(MathHelper.convertV3DV3F(plane4dBrush.getVertices()[0]), MathHelper.convertV3DV3F(plane4dBrush.getVertices()[1]), MathHelper.convertV3DV3F(plane4dBrush.getVertices()[2]), MathHelper.convertV3DV3F(plane4dBrush.getVertices()[3])));
        };
        IEntityModelConstructor<WorldItem> enemyModelConstructor = e -> {
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(new Vector3f(-1.0f, 0.0f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(-1.0f, 2.0f, 0.0f), new Vector3f(1.0f, 2.0f, 0.0f)));
        };
        IEntityModelConstructor<WorldItem> itemPickUpModelConstructor = e -> {
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(new Vector3f(-0.5f, -0.5f, 0.0f), new Vector3f(0.5f, -0.5f, 0.0f), new Vector3f(-0.5f, 0.5f, 0.0f), new Vector3f(0.5f, 0.5f, 0.0f)));
        };

        Material enMat = new Material();
        enMat.setDiffuse(ResourceManager.renderAssets.enemyTexture);

        Material zwMat = new Material();
        zwMat.setDiffuse(ResourceManager.renderAssets.zippo_world);

        Material cwMat = new Material();
        cwMat.setDiffuse(ResourceManager.renderAssets.crowbar_world);

        Material ewMat = new Material();
        ewMat.setDiffuse(ResourceManager.renderAssets.emp_world);

        Material rdMat = new Material();
        rdMat.setDiffuse(ResourceManager.renderAssets.radio_world);

        Material sdMat = new Material();
        sdMat.setDiffuse(ResourceManager.renderAssets.soda_world);

        Material cdMat = new Material();
        cdMat.setDiffuse(ResourceManager.renderAssets.cd_world);

        Material csMat = new Material();
        csMat.setDiffuse(ResourceManager.renderAssets.cassette_world);

        this.water = new RenderLiquidData(ResourceManager.renderAssets.waterNormals, ResourceManager.renderAssets.waterTexture, false, ResourceManager.shaderAssets.world_liquid_gbuffer);

        this.enemy = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_enemy);
        this.enemy.getModelRenderParams().setCustomCullingAABSize(new Vector3d(5.0d));
        this.enemy.setEntityModelConstructor(enemyModelConstructor);
        this.enemy.getModelRenderParams().setShadowCaster(false).setHasTransparency(true);
        this.enemy.setOverObjectMaterial(enMat);

        this.soda_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_pickable);
        this.soda_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.soda_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.soda_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.soda_world.setOverObjectMaterial(sdMat);

        this.radio_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_pickable);
        this.radio_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.radio_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.radio_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.radio_world.setOverObjectMaterial(rdMat);

        this.zippo_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_pickable);
        this.zippo_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.zippo_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.zippo_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.zippo_world.setOverObjectMaterial(zwMat);

        this.emp_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_pickable);
        this.emp_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.emp_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.emp_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.emp_world.setOverObjectMaterial(ewMat);

        this.crowbar_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_pickable);
        this.crowbar_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.crowbar_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.crowbar_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.crowbar_world.setOverObjectMaterial(cwMat);

        this.cd_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_pickable);
        this.cd_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.cd_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.cd_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.cd_world.setOverObjectMaterial(cdMat);

        this.cassette_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_pickable);
        this.cassette_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.cassette_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.cassette_world.getModelRenderParams().setShadowCaster(false).setRenderDistance(64.0f).setHasTransparency(true);
        this.cassette_world.setOverObjectMaterial(csMat);

        this.plank = new RenderObjectData(new RenderObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_gbuffer).setMeshDataGroup(ResourceManager.modelAssets.plank);

        this.door1 = new RenderObjectData(new RenderObject(), WorldItemObject.class, ResourceManager.shaderAssets.world_gbuffer).setMeshDataGroup(ResourceManager.modelAssets.door2);
        this.door1.getModelRenderParams().setShouldInterpolateMovement(false).setCustomCullingAABSize(new Vector3d(5.0d));

        this.entityCube = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world_gbuffer).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.entityLamp = new RenderObjectData(new RenderObject(), LampObject.class, ResourceManager.shaderAssets.world_gbuffer).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.player = new RenderObjectData(new RenderPlayerSP(), PlayerSPObject.class, ResourceManager.shaderAssets.world_gbuffer);

        this.ground = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world_gbuffer);
        this.ground.getModelRenderParams().setAlphaDiscard(0.25f);

        this.particleFlame = new RenderParticleD2Data(new RenderObject(), ParticleObject.class, ResourceManager.shaderAssets.world_gbuffer, ResourceManager.renderAssets.particleTexturePack, true);

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
