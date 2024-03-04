package ru.BouH.engine.game.resources.assets;

import org.joml.Vector2d;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.Material;
import ru.BouH.engine.game.resources.assets.models.basic.MeshHelper;
import ru.BouH.engine.game.resources.assets.models.basic.constructor.IEntityModelConstructor;
import ru.BouH.engine.game.resources.assets.models.mesh.MeshDataGroup;
import ru.BouH.engine.game.resources.cache.GameCache;
import ru.BouH.engine.physics.brush.Plane4dBrush;
import ru.BouH.engine.physics.world.object.WorldItem;
import ru.BouH.engine.render.scene.fabric.render.RenderObject;
import ru.BouH.engine.render.scene.fabric.render.RenderPlayerSP;
import ru.BouH.engine.render.scene.fabric.render.data.RenderLiquidData;
import ru.BouH.engine.render.scene.fabric.render.data.RenderObjectData;
import ru.BouH.engine.render.scene.fabric.render.data.RenderParticleD2Data;
import ru.BouH.engine.render.scene.objects.items.*;

public class RenderDataLoader implements IAssetsLoader {
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

    @Override
    public void load(GameCache gameCache) {
        IEntityModelConstructor<WorldItem> entityModelConstructor = e -> {
            Plane4dBrush plane4dBrush = (Plane4dBrush) e;
            return new MeshDataGroup(MeshHelper.generatePlane3DMesh(plane4dBrush.getVertices()[0], plane4dBrush.getVertices()[1], plane4dBrush.getVertices()[2], plane4dBrush.getVertices()[3]));
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
    }

    @Override
    public LoadMode loadMode() {
        return LoadMode.POST;
    }

    @Override
    public int loadOrder() {
        return 4;
    }
}
