package javagems3d.mapping.processing;

import javagems3d.JGems3D;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.objects.entities.background.SceneBackgroundProp;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsWorldHelper;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.processing.base.MapProcessor;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.entities.kinematic.player.JGemsKinematicPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.Water;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public abstract class ManualMapProcessor extends MapProcessor {
    public ManualMapProcessor() {
        super();
    }

    @Override
    public void init() {
    }

    public static class Default extends ManualMapProcessor {

        @Override
        public void preProcessing(PhysicsWorld world, SceneWorld sceneWorld) {

        }

        @Override
        public void onProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
            MeshBuffer ground2 = this.getLocalResources().createMeshBuffer(new JGemsPath(JGems3D.DEFAULT_PATHS.MODELS, "map04/map04.gltf"), false, false);

            JGemsStaticBody worldModeledBrush = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(ground2), world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
            JGemsWorldHelper.addItemInWorld(worldModeledBrush, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.ground, ground2));
            worldModeledBrush.setPosition(new Vector3f(0, -5, 0));

            Water water = new Water(new Zone(new Vector3f(14.0f, -10.0f, 10.0f), new Vector3f(20.0f, 8.0f, 18.0f)));
            JGemsWorldHelper.addLiquid(water, JGemsResourceManager.globalRenderDataAssets.water);

            JGemsWorldHelper.addPropInScene(new SceneWorldProp("cube", sceneWorld, new PropRenderData(new RenderAttributes(RenderTable.getIndirect(), JGemsRenderProperties.getDefault()), JGemsResourceManager.globalModelAssets.defaultCube_bff)));

            PointLight pointLight = new PointLight(new Vector3f(-20.0f, 0.0f, -12.0f), new Vector3f(1.0f, 0.0f, 0.0f)).setBrightness(10.0f);
            pointLight.on();
            sceneWorld.addLight(pointLight, null);

            PointLight pointLight2 = new PointLight(new Vector3f(-10.0f, 0.0f, -12.0f), new Vector3f(1.0f, 1.0f, 0.0f)).setBrightness(10.0f);
            pointLight2.on();
            sceneWorld.addLight(pointLight2, null);

            PointLight pointLight3 = new PointLight(new Vector3f(0.0f, 0.0f, -12.0f), new Vector3f(1.0f, 0.0f, 1.0f)).setBrightness(10.0f);
            pointLight3.on();
            sceneWorld.addLight(pointLight3, null);
        }

        @Override
        public void postProcessing(PhysicsWorld world, SceneWorld sceneWorld) {

        }

        @Override
        public void onSetupSkyBox(ISkyBox skyBox, SkyBox.Background background) {
            MeshBuffer meshGroup = JGemsResourceManager.getLocalGameResources().createMeshBuffer(new JGemsPath("/assets/jgems/models/skybox_m/city.gltf"), false, false);
            RenderAttributes renderAttributes = new RenderAttributes(RenderTable.getIndirect().replaceShaderManager(Pipeline.SCENE, JGemsResourceManager.globalShaderAssets.background_indirect), JGemsRenderProperties.getDefault());
            renderAttributes.getProperties().setValueFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, 0.5f);
            SceneBackgroundProp sceneProp3 = new SceneBackgroundProp("city", (SceneWorld) background.getWorld(), new PropRenderData(renderAttributes, meshGroup));
            sceneProp3.getModel().getPose().setPosition(new Vector3f(0.0f, -3.0f, 0.0f));
            sceneProp3.getModel().getPose().setRotation(new Vector3f(0.0f, (float) Math.toRadians(0.0f), 0.0f));
            sceneProp3.getModel().getPose().setScaling(new Vector3f(14.0f));
            background.addObjectInBackGround(sceneProp3);
        }

        @Override
        public void onSetupFog(IFogScene fogScene) {

        }

        @Override
        public @Nullable IGameMap.IPlayerConstructor getPlayerConstructor() {
            return (world) -> new Pair<>(new JGemsKinematicPlayer(world, new Vector3f(0.0f), new Vector3f(0.0f)), null);
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
