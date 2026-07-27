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

package workbench.project.game;

import javagems3d.JGems3D;
import javagems3d.system.service.files.JGemsPath;
import org.jetbrains.annotations.NotNull;

public class WBenchGameProject extends GameProjectData {
    private transient JGemsPath currentProjectPath;

    public WBenchGameProject(@NotNull String version, @NotNull String projectTitle) {
        super(JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.GAME_DATA_INFO, projectTitle, version);
    }

    public void setCurrentProjectPath(JGemsPath currentProjectPath1) {
        this.currentProjectPath = currentProjectPath1;
    }

    public JGemsPath getProjectAbsolutePath() {
        return this.currentProjectPath.getAbsolutePathDirectory();
    }

    public JGemsPath getCurrentProjectFilePath() {
        return this.currentProjectPath;
    }

    public String toString() {
        return this.getGameTitle() + " - " + this.getVersion();
    }

    //TODO
    public void checkVersion() {
        //final String version = this.getVersion();
        //if (!JGems3D.DEFAULT_WORKBENCH_PROJECT_CONSTANTS.MAPPING_SUPPORTED_VERSIONS.contains(version)) {
        //    throw new JGemsIOException("Map's version " + version + " is not supported in current JGems3D version: " + JGemsCore.ENG_VER);
        //}
    }
}