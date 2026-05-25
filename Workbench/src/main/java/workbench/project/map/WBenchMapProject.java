package workbench.project.map;

import javagems3d.JGems3D;
import javagems3d.system.external.gaming.JGemsGameInstance;
import javagems3d.system.external.gaming.def.misc.GameResourceScriptAsset;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import javagems3d.system.external.mapping.data.MapProjectData;
import javagems3d.system.service.files.JGemsPath;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import workbench.project.managing.WBenchProjectResourcesManager;

import java.io.File;

public class WBenchMapProject extends MapProjectData {
    public WBenchMapProject(@NotNull String version, @NotNull String projectName, @NotNull JGemsPath absolutePath) {
        super(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_DATA_INFO, projectName, version, absolutePath);
    }

    public JGemsPath getPathToScripts() {
        return JGemsGameInstance.getScriptsFolder(this.getMapAbsolutePath());
    }

    public void refreshScriptFiles() {
        this.scriptFiles = this.readScriptsFolder(this.getPathToScripts().toFile());
        Log.get().debug("Map scripts refreshed...");
    }

    protected GameResourceAssetsFolder<GameResourceScriptAsset> readScriptsFolder(File rootFile) {
        return WBenchProjectResourcesManager.readScriptsFolderRecursive(rootFile, rootFile);
    }
}