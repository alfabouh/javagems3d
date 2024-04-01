package ru.BouH.engine.game.resources.assets;

import org.joml.Vector3d;
import org.joml.Vector3f;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.inventory.items.*;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.Render2D3DObject;
import ru.BouH.engine.render.scene.fabric.render.RenderObject;
import ru.BouH.engine.render.scene.fabric.render.RenderPlayerSP;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.fabric.render.data.RenderParticleD2Data;
import ru.BouH.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.BouH.engine.render.scene.fabric.render.inventory.InventoryCommonItemObject;
import ru.BouH.engine.render.scene.fabric.render.inventory.InventoryEmpObject;
import ru.BouH.engine.render.scene.fabric.render.inventory.InventoryZippoObject;
import ru.BouH.engine.render.scene.objects.items.*;

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
    public RenderObjectData plank;

    public RenderDataLoader() {
        this.inventoryItemRenderTable = new InventoryItemRenderTable();
    }

    @Override
    public void load(GameCache gameCache) {
        Game.getGame().getScreen().addLineInLoadingScreen("Loading render data...");
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

        this.enemy = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world);
        this.enemy.getModelRenderParams().setCustomCullingAABSize(new Vector3d(5.0d));
        this.enemy.setEntityModelConstructor(enemyModelConstructor);
        this.enemy.getModelRenderParams().invertTextureCoordinates().setShadowReceiver(false).setShadowCaster(false).setHasTransparency(true);
        this.enemy.setOverObjectMaterial(enMat);

        this.radio_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world);
        this.radio_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.radio_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.radio_world.getModelRenderParams().invertTextureCoordinates().setLightOpaque(false).setShadowReceiver(false).setShadowCaster(false).setHasTransparency(true);
        this.radio_world.setOverObjectMaterial(rdMat);

        this.zippo_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world);
        this.zippo_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.zippo_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.zippo_world.getModelRenderParams().invertTextureCoordinates().setLightOpaque(false).setShadowReceiver(false).setShadowCaster(false).setHasTransparency(true);
        this.zippo_world.setOverObjectMaterial(zwMat);

        this.emp_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world);
        this.emp_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.emp_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.emp_world.getModelRenderParams().invertTextureCoordinates().setLightOpaque(false).setShadowReceiver(false).setShadowCaster(false).setHasTransparency(true);
        this.emp_world.setOverObjectMaterial(ewMat);

        this.crowbar_world = new RenderObjectData(new Render2D3DObject(), WorldItemObject.class, ResourceManager.shaderAssets.world);
        this.crowbar_world.getModelRenderParams().setCustomCullingAABSize(new Vector3d(2.0d));
        this.crowbar_world.setEntityModelConstructor(itemPickUpModelConstructor);
        this.crowbar_world.getModelRenderParams().invertTextureCoordinates().setLightOpaque(false).setShadowReceiver(false).setShadowCaster(false).setHasTransparency(true);
        this.crowbar_world.setOverObjectMaterial(cwMat);

        this.plank = new RenderObjectData(new RenderObject(), WorldItemObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.plank);

        this.door1 = new RenderObjectData(new RenderObject(), WorldItemObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.door1);
        this.door1.getModelRenderParams().setShouldInterpolateMovement(false).setCustomCullingAABSize(new Vector3d(5.0d));

        this.entityCube = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.entityLamp = new RenderObjectData(new RenderObject(), LampObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.player = new RenderObjectData(new RenderPlayerSP(), PlayerSPObject.class, ResourceManager.shaderAssets.world);

        this.ground = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world);
        this.ground.getModelRenderParams().setAlphaDiscard(0.25f);

        this.particleFlame = new RenderParticleD2Data(new RenderObject(), ParticleObject.class, ResourceManager.shaderAssets.world, ResourceManager.renderAssets.particleTexturePack, true);

        Game.getGame().getScreen().addLineInLoadingScreen("Render data successfully loaded...");
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
