package jgems_app.map;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.core.player.IPlayerConstructor;
import javagems3d.system.map.MapInfo;
import javagems3d.system.map.loaders.IMapLoader;
import javagems3d.system.resources.assets.loading.models.ModelLoaderFlags;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.parsing.space.ParserSpace;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.path.JGemsPath;
import javagems3d.temp.map_sys.save.objects.MapProperties;
import javagems3d.temp.map_sys.save.objects.map_prop.FogProp;
import javagems3d.temp.map_sys.save.objects.map_prop.SkyProp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class TestMap implements IMapLoader {
    @Override
    public void createMap(SystemResources globalResources, SystemResources localResources, PhysicsWorld world, SceneWorld sceneWorld) {
        MeshBuffer meshGroup = localResources.createMeshBuffer(new JGemsPath("/assets/models/sponza/sponza.gltf"), ModelLoaderFlags.DEFAULT, false);
        //sceneWorld.addObjectInWorld(new SceneWorldProp(sceneWorld, new Model3D(new Pose3D(new Vector3f(), new Vector3f(), new Vector3f(0.01f)), meshGroup), RenderAttributes.get()));

        //MeshBuffer meshGroup = localResources.createMeshBuffer(new JGemsPath("/assets/models/map01/map01.gltf"), ModelMeshLoader.ModelLoaderFlags.DEFAULT);
        //JGemsStaticBody worldModeledBrush = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(meshGroup), world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
        //JGemsHelper.WORLD.addItemInWorld(worldModeledBrush, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.ground, meshGroup));
        //worldModeledBrush.setPosition(new Vector3f(0, -5, 0));
        //worldModeledBrush.setRotation(new Vector3f((float) Math.toRadians(-90.0f), 0.0f, 0.0f));
        //worldModeledBrush.setScaling(new Vector3f(0.1f));
    }

    @Override
    public void postLoad(PhysicsWorld world, SceneWorld sceneWorld) {
        //JGemsHelper.CAMERA.enableFreeCamera(JGemsHelper.CONTROLLER.getCurrentController(), new Vector3f(), new Vector3f());
    }

    @Override
    public void preLoad(PhysicsWorld world, SceneWorld sceneWorld) {

    }

    @Override
    public void fillSkyBox(SkyBox.Background background) {
    }

    @Override
    public @Nullable IPlayerConstructor playerConstructor() {
        return null;
        //return (world, startPos, startRot) -> new Pair<>(new TestPlayer(world, startPos, startRot), null);
    }

    @Override
    public @NotNull MapInfo getLevelInfo() {
        SkyProp skyProp = new SkyProp();
        skyProp.setSunPos(new Vector3f(0.35f, 1.0f, 0.125f));
        skyProp.setSunColor(new Vector3f(1.0f, 0.95f, 0.91f));

        FogProp fogProp = new FogProp();
        return new MapInfo(new MapProperties("assets/models/sponza", skyProp, fogProp));
    }
}