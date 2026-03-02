package jgems_app.map;

import javagems3d.graphics.environment.fog.IFogScene;
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

public class TestMap extends ManualMapProcessor {
    @Override
    public void preProcessing(PhysicsWorld world, SceneWorld sceneWorld) {

    }

    @Override
    public void onProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        MeshBuffer meshGroup = this.getLocalResources().createMeshBuffer(new JGemsPathSource(new JGemsPath("/assets/models/sponza/sponza.gltf"), ISource.Source.INSIDE_JAR), false);
        sceneWorld.addObject(new SceneWorldProp("sponza", sceneWorld, new PropRenderData(RenderAttributes.getDefaultIndirect(), meshGroup)));

        JGemsStaticBody worldModeledBrush = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(meshGroup), world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
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
    public void onSetupShadows(IShadowScene shadowScene) {

    }

    @Override
    public void onSetupSkyBox(ISkyBox skyBox, ISkyBackground background) {
        skyBox.getSun().setLightPosition(new Vector3f(0.35f, 1.0f, 0.125f));
        skyBox.getSun().setLightColor(new Vector3f(1.0f, 0.95f, 0.91f));
    }

    @Override
    public void onSetupFog(IFogScene fogScene) {

    }

    @Override
    public @Nullable IGameMap.IPlayerConstructor getPlayerConstructor() {
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