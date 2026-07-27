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

package javagems3d.system.resources.assets.models.helper.forms;

import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.pose.IPose;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.Nullable;

public interface BasicModelCreator<T extends Model<?, ?>> {
    T generateModel(@Nullable ArbitraryArguments arguments);
    RenderMesh generateMesh(@Nullable ArbitraryArguments arguments);
}
