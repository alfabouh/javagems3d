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

package workbench.project.game.settings;

public final class GameProjectSettings {
    public MapProjectAutoSaveMode mapProjectAutoSaveMode;
    public int saveEachStep;
    public float savePerSecond;
    public String compileCorePath;
    public String compileLauncherPath;
    public String compileWorkbenchPath;

    public GameProjectSettings(String absPath) {
        this.mapProjectAutoSaveMode = MapProjectAutoSaveMode.TIMER;
        this.saveEachStep = 5;
        this.savePerSecond = 30.0f;
        this.compileCorePath = absPath + "/jgems3d-core.jar";
        this.compileLauncherPath = absPath + "/jgems3d-launcher.jar";
        this.compileWorkbenchPath = absPath + "/jgems3d-workbench.jar";
    }

    public enum MapProjectAutoSaveMode {
        TIMER,
        STEPS
    }
}
