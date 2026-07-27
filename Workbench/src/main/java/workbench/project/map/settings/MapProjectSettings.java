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

package workbench.project.map.settings;

public final class MapProjectSettings {
    public boolean VIEW_SHADOWS = true;
    public boolean VIEW_CHESS_TERRAIN = true;
    public boolean WIREFRAME_RENDERING = false;
    public boolean VIEW_HDR = true;
    public boolean ANIMATIONS = true;
    public boolean VIEW_FOG = true;
    public boolean FULL_BRIGHT = false;
    public float cameraX;
    public float cameraY;
    public float cameraZ;
    public float cameraRotX;
    public float cameraRotY;
    public float cameraRotZ;

    public MapProjectSettings() {
        this.cameraX = 0.0f;
        this.cameraY = 5.0f;
        this.cameraZ = 0.0f;
        this.cameraRotX = 0.0f;
        this.cameraRotY = 0.0f;
        this.cameraRotZ = 0.0f;
    }
}
