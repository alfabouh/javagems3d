package A_default_app.map;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.lights.scene.ILightScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.external.mapping.IGameMap;
import javagems3d.system.external.mapping.processing.ManualMapProcessor;
import javagems3d.physics.colliders.MeshCollider;
import javagems3d.physics.entities.bullet.bodies.JGemsStaticBody;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.managing.JGemsResourceManager;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;

public class TestMap extends ManualMapProcessor {
    @Override
    public void preProcessing(PhysicsWorld world, SceneWorld sceneWorld) {

    }

    @Override
    public void onProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        long stop = System.currentTimeMillis();
        final JGemsPath trees                           = new JGemsPath("/assets/models/trees/trees.gltf");
        final JGemsPath flatgrass                         = new JGemsPath("/assets/models/flatgrass/flatgrass.gltf");
        final JGemsPath flatgrass_back           = new JGemsPath("/assets/models/flatgrass/flatgrass_back.gltf");
        final JGemsPath Gate_2x4                         = new JGemsPath("/assets/models/castle1/Gate_2x4.gltf");
        final JGemsPath Gate_2x4_Beveled             = new JGemsPath("/assets/models/castle1/Gate_2x4_Beveled.gltf");
        final JGemsPath Gate_2x4_doorway             = new JGemsPath("/assets/models/castle1/Gate_2x4_doorway.gltf");
        final JGemsPath Gate_Door                         = new JGemsPath("/assets/models/castle1/Gate_Door.gltf");
        final JGemsPath Roof_Cone                         = new JGemsPath("/assets/models/castle1/Roof_Cone.gltf");
        final JGemsPath Roof_Cube                         = new JGemsPath("/assets/models/castle1/Roof_Cube.gltf");
        final JGemsPath Roof_rectangle             = new JGemsPath("/assets/models/castle1/Roof_rectangle.gltf");
        final JGemsPath Scaffle                         = new JGemsPath("/assets/models/castle1/Gate_2x4.gltf");
        final JGemsPath Scaffle_Ramp             = new JGemsPath("/assets/models/castle1/Scaffle_Ramp.gltf");
        final JGemsPath Tower_Doorway             = new JGemsPath("/assets/models/castle1/Tower_Doorway.gltf");
        final JGemsPath Tower_Mid                         = new JGemsPath("/assets/models/castle1/Tower_Mid.gltf");
        final JGemsPath Tower_mid_hollow             = new JGemsPath("/assets/models/castle1/Tower_mid_hollow.gltf");
        final JGemsPath Tower_mid_hollow_ruined             = new JGemsPath("/assets/models/castle1/Tower_mid_hollow_ruined.gltf");
        final JGemsPath Tower_top_1                         = new JGemsPath("/assets/models/castle1/Tower_top_1.gltf");
        final JGemsPath Wall_2x2                         = new JGemsPath("/assets/models/castle1/Roof_Cube.gltf");
        final JGemsPath Wall_2x2_walkway             = new JGemsPath("/assets/models/castle1/Wall_2x2_walkway.gltf");
        final JGemsPath Wall_2x4                         = new JGemsPath("/assets/models/castle1/Wall_2x4.gltf");
        final JGemsPath Wall_2x4_ruined             = new JGemsPath("/assets/models/castle1/Wall_2x4_ruined.gltf");
        final JGemsPath Wall_2x4_walkway             = new JGemsPath("/assets/models/castle1/Wall_2x4_walkway.gltf");
        final JGemsPath Wall_2x4_walkway_beveled             = new JGemsPath("/assets/models/castle1/Wall_2x4_walkway_beveled.gltf");
        final JGemsPath Wall_corner_walkwayh             = new JGemsPath("/assets/models/castle1/Wall_corner_walkwayh.gltf");
        final JGemsPath Window_bars                        = new JGemsPath("/assets/models/castle1/Roof_Cube.gltf");
        final JGemsPath Window_glass             = new JGemsPath("/assets/models/castle1/Window_glass.gltf");

        this.getLocalResources().createMeshBuffer(new JGemsPathSource(trees                   , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(flatgrass               , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(flatgrass_back          , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Gate_2x4                , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Gate_2x4_Beveled        , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Gate_2x4_doorway        , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Gate_Door               , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Roof_Cone               , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Roof_Cube               , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Roof_rectangle          , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Scaffle                 , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Scaffle_Ramp            , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Tower_Doorway           , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Tower_Mid               , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Tower_mid_hollow        , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Tower_mid_hollow_ruined , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Tower_top_1             , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Wall_2x2                , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Wall_2x2_walkway        , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Wall_2x4                , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Wall_2x4_ruined         , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Wall_2x4_walkway        , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Wall_2x4_walkway_beveled, ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Wall_corner_walkwayh    , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Window_bars             , ISource.Source.INSIDE_JAR), false);
        this.getLocalResources().createMeshBuffer(new JGemsPathSource(Window_glass            , ISource.Source.INSIDE_JAR), false);

        MeshBuffer meshGroup = this.getLocalResources().createMeshBuffer(new JGemsPathSource(new JGemsPath("/assets/models/sponza/glTF/sponza.gltf"), ISource.Source.INSIDE_JAR), false);
        sceneWorld.addObject(new SceneWorldProp("sponza", sceneWorld, new PropRenderData(RenderAttributes.getDefaultIndirect(), meshGroup)));
        System.out.println(System.currentTimeMillis() - stop);

        JGemsStaticBody worldModeledBrush = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(meshGroup), world, new Vector3f(0.0f), "grass").setCanBeDeleted(false);
        JGemsHelper.world().addWorldItem(worldModeledBrush, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.ground, meshGroup));
        worldModeledBrush.setPosition(new Vector3f(0, -5, 0));
        worldModeledBrush.setRotation(new Vector3f((float) Math.toRadians(-90.0f), 0.0f, 0.0f));
        worldModeledBrush.setScaling(new Vector3f(0.1f));
    }

    @Override
    public void postProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        JGemsHelper.camera().enableFreeCamera(JGemsHelper.controller().getCurrentController(), new Vector3f(), new Vector3f());
    }

    @Override
    public void onSetupLighting(ILightScene lightScene, IEnvironment environment) {

    }

    @Override
    public void onSetupShadows(IShadowScene shadowScene, IEnvironment environment) {

    }

    @Override
    public void onSetupSkyBox(ISkyBox skyBox, ISkyBackground background, IEnvironment environment) {
        skyBox.getSun().setLightPosition(new Vector3f(0.35f, 1.0f, 0.125f));
        skyBox.getSun().setLightColor(new Vector3f(1.0f, 0.95f, 0.91f));
    }

    @Override
    public void onSetupFog(IFogScene fogScene, IEnvironment environment) {

    }

    @Override
    public @Nullable IGameMap.IPlayerConstructor getPlayerConstructor(PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
        return null;
    }

    @Override
    public @Nullable Collection<IGameMap.SpawnPlayerData> getSpawnPlayersSet() {
        return null;
    }

    @Override
    public @NotNull String getMapName() {
        return "sponza";
    }

    @Override
    public @NotNull String getMapInformation() {
        return "Default";
    }
}