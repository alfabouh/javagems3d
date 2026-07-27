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

package workbench.global;

import javagems3d.JGems3D;

public abstract class WBenchConstants {
    public static float CAM_SENS = 0.001f;
    public static float FOV = (float) Math.toRadians(60.0f);
    public static float Z_NEAR = 0.1f;
    public static float Z_FAR = (float) JGems3D.MAP_MAX_SIZE * 8.0f;
}
