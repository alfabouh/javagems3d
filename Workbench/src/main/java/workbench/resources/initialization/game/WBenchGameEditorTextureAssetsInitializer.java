package workbench.resources.initialization.game;

import api.application.workbench.manager.APIWBenchDataManager;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;
import workbench.WBench;
import java.util.Map;

public class WBenchGameEditorTextureAssetsInitializer implements IAssetsInitializer {
    public void load(SystemResources systemResources) {
        //APIWBenchDataManager apiwBenchDataManager = WBench.APIEditorResources().getEditorResourcesManager();
        //Map<String, Pair<String, JGemsPath>> map = apiwBenchDataManager.getSkyBoxesMap();
        //for (Map.Entry<String, Pair<String, JGemsPath>> entry : map.entrySet()) {
        //    ICubeMapProgram cubeMapProgram = systemResources.createCubeMapTexture(JGems3D.GetSource.EXTERNAL, null, entry.getValue().getSecond(), entry.getValue().getFirst(), new CubeMapTexture.Properties(true));
        //    if (cubeMapProgram != null) {
        //        WBench.get().getMapProjectManager().getMapObjectTemplates().addSkyBox(entry.getKey(), cubeMapProgram);
        //    }
        //}
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
