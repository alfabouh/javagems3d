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

package javagems3d.system.map.loaders.custom;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.entities.background.SceneBackgroundProp;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.system.resources.assets.loading.models.ModelMeshLoader;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshBuffer;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.Water;
import javagems3d.system.map.MapInfo;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.managing.resources.GameResources;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.temp.map_sys.save.objects.MapProperties;
import javagems3d.temp.map_sys.save.objects.map_prop.FogProp;
import javagems3d.temp.map_sys.save.objects.map_prop.SkyProp;

public class DefaultMap implements IMapLoader {
    public DefaultMap() {
    }

    @Override
    public void createMap(GameResources globalResources, GameResources localResources, PhysicsWorld world, SceneWorld sceneWorld) {
        //world.setMapNavGraph(Graph.readFromFile(new JGemsPath("/assets/jgems/nav.mesh")));

        JGemsStaticBody worldModeledBrush = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(JGemsResourceManager.globalModelAssets.ground2), world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
        JGemsHelper.WORLD.addItemInWorld(worldModeledBrush, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.ground, JGemsResourceManager.globalModelAssets.ground2));
        worldModeledBrush.setPosition(new Vector3f(0, -5, 0));

        Water water = new Water(new Zone(new Vector3f(14.0f, -10.0f, 10.0f), new Vector3f(20.0f, 8.0f, 18.0f)));
        JGemsHelper.WORLD.addLiquid(water, JGemsResourceManager.globalRenderDataAssets.water);

        //CubeAI cubeAI = new CubeAI(world, new Vector3f(0.0f), "grass");
        //JGemsHelper.WORLD.addItemInWorld(cubeAI, new RenderEntityData(JGemsResourceManager.globalRenderDataAssets.entityCube, JGemsResourceManager.globalModelAssets.cube));

        //EntityCollectableItem collectableItem = new EntityCollectableItem(world, new ItemZippo(), new Vector3f(3.0f, -4.5f, 0.0f), "zippo");
        //JGemsHelper.WORLD.addItemInWorld(collectableItem, JGemsResourceManager.globalRenderDataAssets.zippo_world);
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {
    }

    @Override
    public void preLoad(PhysicsWorld world, SceneWorld sceneWorld) {
    }

    @Override
    public void fillSkyBox(SkyBox.Background background) {
        //MeshGroup meshGroup = JGemsResourceManager.getLocalGameResources().createMesh(new JGemsPath("/assets/jgems/models/skybox_m/city.obj"), ModelMeshLoader.FLAGS.RETURN_BAKED_RENDER_MESH_GROUP).getFirst();
        //SceneProp sceneProp3 = new SceneProp(new RenderSimpleBackgroundProp(background), new Model<>(new Format3D(new Vector3f(0.0f, -3.0f, 0.0f), new Vector3f(0.0f, (float) Math.toRadians(0.0f), 0.0f), new Vector3f(1.0f)), meshGroup), JGemsResourceManager.globalShaderAssets.skybox_background);
        //sceneProp3.getRenderAttributes().setAlphaDiscardValue(0.5f);
        //background.addObjectInBackGround(sceneProp3);

        MeshBuffer meshGroup = JGemsResourceManager.getLocalGameResources().createMeshBuffer(new JGemsPath("/assets/jgems/models/skybox_m/city.obj"), ModelMeshLoader.FLAGS.LOAD_IN_INDIRECT_BUFFER);
        SceneBackgroundProp sceneProp3 = new SceneBackgroundProp(background.getSceneWorld(), new Model<>(new Format3D(new Vector3f(0.0f, -3.0f, 0.0f), new Vector3f(0.0f, (float) Math.toRadians(0.0f), 0.0f), new Vector3f(1.0f)), meshGroup), RenderAttributes.get(new RenderTable(JGemsResourceManager.globalShaderAssets.background_indirect, RenderTable.DEFAULT_SCENE_RENDER_FABRIC_IND, false)));
        sceneProp3.getRenderAttributes().setAlphaDiscardValue(0.5f);
        background.addObjectInBackGround(sceneProp3);
    }

    @Override
    public @NotNull MapInfo getLevelInfo() {
        return new MapInfo(new MapProperties("default", new SkyProp(), new FogProp()));
    }
}
