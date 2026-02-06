package jgems_app.map;

import javagems3d.graphics.environment.fog.IFogScene;
import javagems3d.graphics.environment.shadows.scene.IShadowScene;
import javagems3d.graphics.environment.skybox.ISkyBox;
import javagems3d.graphics.environment.skybox.background.ISkyBackground;
import javagems3d.graphics.objects.entities.world.SceneWorldProp;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.mapping.IGameMap;
import javagems3d.mapping.processing.ManualMapProcessor;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class TestMapDirect extends ManualMapProcessor {
    @Override
    public void preProcessing(PhysicsWorld world, SceneWorld sceneWorld) {

    }

    @Override
    public void onProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        MeshBuffer cube = JGemsResourceManager.globalModelAssets.grassCube;

        int size = 15;
        float spacing = 1.5f;

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    Vector3f position = new Vector3f(x * spacing, y * spacing, z * spacing);
                    SceneWorldProp sceneWorldProp = new SceneWorldProp("cubetest", sceneWorld, new PropRenderData(new RenderAttributes(RenderTable.getIndirect(), JGemsRenderProperties.getDefault()), cube));
                    sceneWorld.addObject(sceneWorldProp);
                    sceneWorldProp.getModel().getPose().setPosition(position);
                }
            }
        }

     //  SceneWorldProp sceneWorldProp = new SceneWorldProp("cube", sceneWorld, new PropRenderData(new RenderAttributes(RenderTable.getIndirect(), JGemsRenderProperties.getDefault()), JGemsResourceManager.globalModelAssets.defaultCube_bff));
     //  JGemsHelper.world().addProp(sceneWorldProp);
     //  sceneWorldProp.getModel().getPose().setPosition(new Vector3f(0f, 1f, 0f));

        //JGemsStaticBody worldModeledBrush = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(meshGroup), world, new Vector3f(0.0f), "grass").setCanBeDestroyed(false);
        //JGemsHelper.world().addWorldItem(worldModeledBrush, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.ground, meshGroup));
        //worldModeledBrush.setPosition(new Vector3f(0, -5, 0));
        //worldModeledBrush.setRotation(new Vector3f((float) Math.toRadians(-90.0f), 0.0f, 0.0f));
        //worldModeledBrush.setScaling(new Vector3f(0.1f));
    }

    @Override
    public void postProcessing(PhysicsWorld world, SceneWorld sceneWorld) {
        //JGemsHelper.camera().enableFreeCamera(JGemsHelper.controller().getCurrentController(), new Vector3f(), new Vector3f());
    }

    @Override
    public void onSetupShadows(IShadowScene shadowScene) {

    }

    @Override
    public void onSetupSkyBox(ISkyBox skyBox, ISkyBackground background) {
        skyBox.setSky2DTexture(JGemsResourceManager.globalTextureAssets.defaultSkyboxCubeMap);
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
        return "dirtest";
    }

    @Override
    public @NotNull String getMapInformation() {
        return "Default";
    }
}