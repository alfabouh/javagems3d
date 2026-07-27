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

package workbench.graphics.environment.components.decals;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.environment.decals.fx.WorldDefaultDecalFX;
import javagems3d.graphics.environment.decals.scene.DecalsScene;
import javagems3d.graphics.environment.decals.scene.JGemsDecalsScene;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.resources.WBenchResourceManager;

public class WBenchDecalsScene extends JGemsDecalsScene {
    public WBenchDecalsScene(IEnvironment environment) {
        super(environment);
    }
}
