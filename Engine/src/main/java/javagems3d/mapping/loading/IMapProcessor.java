package javagems3d.mapping.loading;

import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.resources.managing.resources.SystemResources;

public interface IMapProcessor {
    void preProcessing(PhysicsWorld world, SceneWorld sceneWorld);
    void onProcessing(PhysicsWorld world, SceneWorld sceneWorld);
    void postProcessing(PhysicsWorld world, SceneWorld sceneWorld);

    void onSetupSkyBoxBackground(SkyBox.Background background);
  //  void onSetupSkyBoxCubeMap(JGemsPath);



    SystemResources getGlobalResources();
    SystemResources getLocalResources();
}
