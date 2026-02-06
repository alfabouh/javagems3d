package workbench.resources.initialization.map;

import api.application.workbench.manager.APIWBenchDataManager;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.loading.samples.CubeMapsLoader;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.files.JGemsPath;
import javagems3d.system.service.files.source.JGemsStringSource;
import workbench.WBench;
import workbench.project.managing.instances.group.GameResourceAssetsFolder;
import workbench.project.managing.instances.mapping.GameResourceSkyboxAsset;
import workbench.project.managing.instances.world.GameResourceEntityObjectAsset;
import workbench.project.map.MapObjectTemplatesManager;

import java.util.*;

public class WBenchMapEditorTextureAssetsInitializer implements IAssetsInitializer {
    public void load(SystemResources systemResources) {
        final APIWBenchDataManager apiwBenchDataManager = WBench.APIEditorResources().getEditorResourcesManager();
        final GameResourceAssetsFolder<GameResourceSkyboxAsset> skyboxAssets = WBench.get().getGameProjectManager().getGameResourcesManager().getSkyBoxesAssetsFolder();

        Map<String, ICubeMapProgram.CMTextures> map = apiwBenchDataManager.getSkyBoxesMap();
        for (Map.Entry<String, ICubeMapProgram.CMTextures> entry : map.entrySet()) {
            ICubeMapProgram cubeMapProgram = systemResources.createCubeMapTexture(null, new CubeMapsLoader.CubeMapTexturesContainer(entry.getValue()), new CubeMapTexture.Properties(true));
            if (cubeMapProgram != null) {
                WBench.get().getMapProjectManager().getMapObjectTemplates().addSkyBox(entry.getKey(), new MapObjectTemplatesManager.SkyBoxTemplate(entry.getKey(), entry.getValue()).setCubeMapProgram(cubeMapProgram));
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
