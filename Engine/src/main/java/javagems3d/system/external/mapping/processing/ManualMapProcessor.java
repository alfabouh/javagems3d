package javagems3d.system.external.mapping.processing;

import javagems3d.JGems3D;
import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.processing.base.MapProcessor;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.entities.kinematic.player.JGemsKinematicPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.Water;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;

public abstract class ManualMapProcessor extends MapProcessor {
    public ManualMapProcessor() {
        super();
    }

    @Override
    public void init() {
    }

    @Deprecated
    public static class Default extends ManualMapProcessor {

        @Override
        public void preProcessing(PhysicsWorld world, SceneWorld sceneWorld) {

        }

        @Override
        public void onProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
            MeshBuffer ground2 = this.getLocalResources().createMeshBuffer(new JGemsPathSource(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "map04/map04.gltf"), ISource.Source.INSIDE_JAR), false);

            JGemsStaticBody worldModeledBrush = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(ground2), world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
            JGemsHelper.world().addWorldItem(worldModeledBrush, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.ground, ground2));
            worldModeledBrush.setPosition(new Vector3f(0, -5, 0));

            Water water = new Water(new Zone(new Vector3f(14.0f, -10.0f, 10.0f), new Vector3f(20.0f, 8.0f, 18.0f)));
            JGemsHelper.world().addLiquid(water, JGemsResourceManager.globalRenderDataAssets.water);

            PointLight pointLight = new PointLight(new Vector3f(-20.0f, 0.0f, -12.0f), new Vector3f(1.0f, 0.0f, 0.0f)).setBrightness(10.0f);
            pointLight.on();
           // sceneWorld.addLight(pointLight, null);

            PointLight pointLight2 = new PointLight(new Vector3f(-10.0f, 0.0f, -12.0f), new Vector3f(1.0f, 1.0f, 0.0f)).setBrightness(10.0f);
            pointLight2.on();
           // sceneWorld.addLight(pointLight2, null);

            PointLight pointLight3 = new PointLight(new Vector3f(0.0f, 0.0f, -12.0f), new Vector3f(1.0f, 0.0f, 1.0f)).setBrightness(10.0f);
            pointLight3.on();
          //  sceneWorld.addLight(pointLight3, null);
        }

        @Override
        public void postProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        }

        @Override
        public void onSetupLighting(ILightScene lightScene, IEnvironment environment) {

        }

        @Override
        public void onSetupShadows(IShadowScene shadowScene, IEnvironment environment) {
        }

        @Override
        public void onSetupSkyBox(ISkyBox skyBox, ISkyBackground background, IEnvironment environment) {
            skyBox.setSky2DTexture(JGemsResourceManager.globalTextureAssets.defaultSkyboxCubeMap);
        }

        @Override
        public void onSetupFog(IFogScene fogScene, IEnvironment environment) {
        }

        @Override
        public @Nullable IGameMap.IPlayerConstructor getPlayerConstructor(PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            return ExternalMapProcessor.Default.getDefaultPlayerConstructor();
        }

        @Override
        public @Nullable Collection<IGameMap.SpawnPlayerData> getSpawnPlayersSet() {
            return IGameMap.SpawnPlayerData.createSingle(new Vector3f(), new Vector3f(0.0f, 0.0f, 0.0f));
        }

        @Override
        public @NotNull String getMapName() {
            return "Default";
        }

        @Override
        public @NotNull String getMapInformation() {
            return "Default Map";
        }
    }
}
