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

package javagems3d.graphics.objects.entities.world;

import javagems3d.graphics.objects.entities.SceneProp;
import javagems3d.graphics.objects.rendering.constructors.IModelConstructor;
import javagems3d.graphics.objects.rendering.data.PropRenderData;
import javagems3d.graphics.world.SceneWorld;

import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public class SceneWorldProp extends SceneProp {
    public SceneWorldProp(@NotNull String name, @NotNull SceneWorld sceneWorld, @NotNull PropRenderData propRenderData) {
        super(name, sceneWorld, propRenderData);
    }
}
