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

package javagems3d.system.resources.assets.loading.models.gltf.parsing;

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Asset;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Node;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Scene;

import java.util.List;

public final class GLTF2RawData {
    private final GLTF2Asset gltf2Asset;
    private final GLTF2Scene scene;

    public GLTF2RawData(GLTF2Asset gltf2Asset, GLTF2Scene scene) {
        this.gltf2Asset = gltf2Asset;
        this.scene = scene;
    }

    public GLTF2Asset getGltf2Asset() {
        return this.gltf2Asset;
    }

    public GLTF2Scene getGltf2Scene() {
        return this.scene;
    }
}
