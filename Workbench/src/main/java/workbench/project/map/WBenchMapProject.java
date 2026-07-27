/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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