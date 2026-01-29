package workbench.resources.initialization.game;

import api.application.workbench.manager.APIWBenchDataManager;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.initialization.base.IAssetsInitializer;
import javagems3d.system.resources.assets.texturing.maps.CubeMapTexture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.ResourceManager;
import javagems3d.system.resources.managing.resources.SystemResources;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.path.JGemsPath;
import workbench.WBench;
import java.util.Map;

public class WBenchGameEditorTextureAssetsInitializer implements IAssetsInitializer {
    public static ITexture2DProgram hintBACK;
    public static ITexture2DProgram hintUP;
    public static ITexture2DProgram hintFRONT;
    public static ITexture2DProgram hintBOTTOM;
    public static ITexture2DProgram hintRIGHT;
    public static ITexture2DProgram hintLEFT;

    public void load(SystemResources systemResources) {
        WBenchGameEditorTextureAssetsInitializer.hintBACK = systemResources.createTexture(JGems3D.GetSource.JAR, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath("/assets/wbench/textures/BACK.png"), new ImageTexture.Properties(true, true));
        WBenchGameEditorTextureAssetsInitializer.hintUP = systemResources.createTexture(JGems3D.GetSource.JAR, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath("/assets/wbench/textures/UP.png"), new ImageTexture.Properties(true, true));
        WBenchGameEditorTextureAssetsInitializer.hintFRONT = systemResources.createTexture(JGems3D.GetSource.JAR, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath("/assets/wbench/textures/FRONT.png"), new ImageTexture.Properties(true, true));
        WBenchGameEditorTextureAssetsInitializer.hintBOTTOM = systemResources.createTexture(JGems3D.GetSource.JAR, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath("/assets/wbench/textures/BOTTOM.png"), new ImageTexture.Properties(true, true));
        WBenchGameEditorTextureAssetsInitializer.hintRIGHT = systemResources.createTexture(JGems3D.GetSource.JAR, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath("/assets/wbench/textures/RIGHT.png"), new ImageTexture.Properties(true, true));
        WBenchGameEditorTextureAssetsInitializer.hintLEFT = systemResources.createTexture(JGems3D.GetSource.JAR, ResourceManager.DEFAULT_TEXTURE(), new JGemsPath("/assets/wbench/textures/LEFT.png"), new ImageTexture.Properties(true, true));

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
