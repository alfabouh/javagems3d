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

package javagems3d.system.external.mapping.data;

import javagems3d.system.external.gaming.JGemsGameInstance;
import javagems3d.system.external.gaming.def.misc.GameResourceScriptAsset;
import javagems3d.system.external.gaming.def.util.GameResourceAssetsFolder;
import javagems3d.system.service.files.JGemsPath;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("all")
public class MapProjectData {
    private transient JGemsPath absolutePath;
    protected String mapDescription;
    protected String mapName;
    protected String version;
    protected transient GameResourceAssetsFolder<GameResourceScriptAsset> scriptFiles;

    public MapProjectData(@NotNull String mapDescription, @NotNull String mapName, @NotNull String version, @NotNull JGemsPath absolutePath) {
        this.setAbsolutePath(absolutePath);
        this.mapDescription = mapDescription;
        this.mapName = mapName;
        this.version = version;
        this.scriptFiles = new GameResourceAssetsFolder<>("");
    }

    public MapProjectData setAbsolutePath(@NotNull JGemsPath absolutePath) {
        this.absolutePath = absolutePath;
        return this;
    }

    public JGemsPath getPathToMainMapFile() {
        return JGemsGameInstance.getPathToMainMapFile(this.getMapAbsolutePath(), this.getMapName());
    }

    public JGemsPath getPathToDataMapFile() {
        return JGemsGameInstance.getPathToDataMapFile(this.getMapAbsolutePath(), this.getMapName());
    }

    public MapProjectData setMapDescription(String mapDescription) {
        this.mapDescription = mapDescription;
        return this;
    }

    public JGemsPath getMapAbsolutePath() {
        return this.absolutePath;
    }

    public String getMapDescription() {
        return this.mapDescription;
    }

    public String getMapName() {
        return this.mapName;
    }

    public String getVersion() {
        return this.version;
    }

    public GameResourceAssetsFolder<GameResourceScriptAsset> getScriptFiles() {
        return this.scriptFiles;
    }

    public String toString() {
        return this.getMapName() + " - " + this.getVersion();
    }

    //TODO
    public void checkVersion() {
        //final String version = this.getVersion();
        //if (!JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_SUPPORTED_VERSIONS.contains(version)) {
        //    throw new JGemsIOException("Map's version " + version + " is not supported in current JGems3D version: " + JGemsCore.ENG_VER);
        //}
    }
}
