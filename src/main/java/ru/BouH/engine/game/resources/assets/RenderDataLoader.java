package ru.BouH.engine.game.resources.assets;

import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.inventory.items.InventoryItem;
import ru.BouH.engine.inventory.items.ItemZippo;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.RenderEnemy;
import ru.BouH.engine.render.scene.fabric.render.RenderObject;
import ru.BouH.engine.render.scene.fabric.render.RenderPlayerSP;
import ru.BouH.engine.render.scene.fabric.render.data.RenderLiquidData;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.fabric.render.data.RenderParticleD2Data;
import ru.BouH.engine.render.scene.fabric.render.data.inventory.RenderInventoryItemData;
import ru.BouH.engine.render.scene.fabric.render.inventory.InventoryZippoObject;
import ru.BouH.engine.render.scene.objects.items.*;

import java.util.HashMap;
import java.util.Map;

public class RenderDataLoader implements IAssetsLoader {
    public final InventoryItemRenderTable inventoryItemRenderTable;
    public RenderObjectData entityCube;
    public RenderObjectData entityCube2;
    public RenderObjectData entityLargeCube;
    public RenderObjectData entityLamp;
    public RenderObjectData player;
    public RenderObjectData plane;
    public RenderObjectData planeGround;
    public RenderObjectData ground;
    public RenderObjectData test;
    public RenderLiquidData renderLiquidData;
    public RenderObjectData particleFlame;
    public RenderObjectData door1;
    public RenderObjectData enemy;

    public RenderDataLoader() {
        this.inventoryItemRenderTable = new InventoryItemRenderTable();
    }

    @Override
    public void load(GameCache gameCache) {
        Game.getGame().getScreen().addLineInLoadingScreen("Loading render data...");
        this.inventoryItemRenderTable.addItem(ItemZippo.class, new RenderInventoryItemData(ResourceManager.shaderAssets.inventory_zippo, new InventoryZippoObject()));

        IEntityModelConstructor<WorldItem> entityModelConstructor = e -> {
            Plane4dBrush plane4dBrush = (Plane4dBrush) e;
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(plane4dBrush.getVertices()[0], plane4dBrush.getVertices()[1], plane4dBrush.getVertices()[2], plane4dBrush.getVertices()[3]));
        };
        IEntityModelConstructor<WorldItem> enemyModelConstructor = e -> {
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(new Vector3d(-1.0d, 0.0d, 0.0d), new Vector3d(1.0d, 0.0d, 0.0d), new Vector3d(-1.0d, 2.0d, 0.0d), new Vector3d(1.0d, 2.0d, 0.0d)));
        };

        Material tallGrassPlane = new Material();
        tallGrassPlane.setDiffuse(ResourceManager.renderAssets.tallGrass);

        Material grassPlane = new Material();
        grassPlane.setDiffuse(ResourceManager.renderAssets.grassTexture);
        grassPlane.setNormals(ResourceManager.renderAssets.grassNormals);
        grassPlane.setSpecular(ResourceManager.renderAssets.grassSpecular);

        Material brickPlane = new Material();
        brickPlane.setDiffuse(ResourceManager.renderAssets.bricksTexture);
        brickPlane.setNormals(ResourceManager.renderAssets.bricksNormals);

        Material enMat = new Material();
        enMat.setDiffuse(ResourceManager.renderAssets.enemyTexture);

        this.enemy = new RenderObjectData(new RenderEnemy(), WorldItemObject.class, ResourceManager.shaderAssets.world);
        this.enemy.getModelRenderParams().setCustomCullingAABSize(new Vector3d(5.0d));
        this.enemy.setEntityModelConstructor(enemyModelConstructor);
        this.enemy.getModelRenderParams().invertTextureCoordinates().setShadowReceiver(false).setShadowCaster(false).setHasTransparency(true);
        this.enemy.setOverObjectMaterial(enMat);

        this.door1 = new RenderObjectData(new RenderObject(), WorldItemObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.door1);
        this.door1.getModelRenderParams().setShouldInterpolateMovement(false).setCustomCullingAABSize(new Vector3d(5.0d));

        this.entityCube = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.entityCube2 = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);

        this.entityLargeCube = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.entityLamp = new RenderObjectData(new RenderObject(), LampObject.class, ResourceManager.shaderAssets.world).setMeshDataGroup(ResourceManager.modelAssets.cube);
        this.player = new RenderObjectData(new RenderPlayerSP(), PlayerSPObject.class, ResourceManager.shaderAssets.world);
        this.test = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.simple).setEntityModelConstructor(entityModelConstructor);
        this.plane = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world).setEntityModelConstructor(entityModelConstructor).setModelTextureScaling(new Vector2d(64.0d, 4.0d));
        this.planeGround = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world).setEntityModelConstructor(entityModelConstructor).setModelTextureScaling(new Vector2d(128.0d));

        this.ground = new RenderObjectData(new RenderObject(), EntityObject.class, ResourceManager.shaderAssets.world);
        this.ground.getModelRenderParams().setAlphaDiscard(0.25f);

        this.particleFlame = new RenderParticleD2Data(new RenderObject(), ParticleObject.class, ResourceManager.shaderAssets.world, ResourceManager.renderAssets.particleTexturePack, true);

        this.renderLiquidData = new RenderLiquidData(ResourceManager.renderAssets.grassNormals, ResourceManager.renderAssets.waterTexture, ResourceManager.renderAssets.skyboxCubeMap, ResourceManager.shaderAssets.liquid);

        this.test.getModelRenderParams().setShadowCaster(false);
        this.plane.setOverObjectMaterial(brickPlane);
        this.planeGround.setOverObjectMaterial(grassPlane);

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
