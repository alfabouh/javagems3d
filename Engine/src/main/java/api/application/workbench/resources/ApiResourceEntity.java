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

package api.application.workbench.resources;

import api.application.workbench.resources.data.jgems.JGemsEntityData;
import api.application.workbench.resources.data.wbench.MapObjectsIdentifiers;
import api.application.workbench.resources.data.wbench.WBenchObjectData;
import org.jetbrains.annotations.NotNull;

public class ApiResourceEntity extends APIResource<WBenchObjectData, JGemsEntityData> {
    public ApiResourceEntity(@NotNull String id, @NotNull APIResource.MapObjectFabric<WBenchObjectData> fabricWBench, @NotNull APIResource.MapObjectFabric<JGemsEntityData> fabricGame) {
        super(MapObjectsIdentifiers.ENTITY + id, fabricWBench, fabricGame);
    }
}
