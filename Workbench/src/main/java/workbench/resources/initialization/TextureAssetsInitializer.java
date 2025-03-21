package workbench.resources.initialization;

import api.application.workbench.manager.APIWBenchDataManager;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Triple;
import javagems3d.system.service.path.JGemsPath;
import workbench.WBench;

import java.util.Set;

public class TextureAssetsInitializer implements IAssetsInitializer {
    public void load(SystemResources systemResources) {
        APIWBenchDataManager apiwBenchDataManager = WBench.APIEditorResources().getEditorResourcesManager();
        Set<Triple<String, String, JGemsPath>> entityEntry = apiwBenchDataManager.getSkyBoxesPath();
        for (Triple<String, String, JGemsPath> entry : entityEntry) {
            ICubeMapProgram cubeMapProgram = systemResources.createCubeMapTexture(null, entry.getThird(), entry.getSecond(), new CubeMapTexture.Properties(true));
            if (cubeMapProgram != null) {
                WBench.get().getProjectObjects().addSkyBox(entry.getFirst(), cubeMapProgram);
            }
        }
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.HIGH;
    }
}
