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

package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import java.util.ArrayList;
import java.util.List;

public final class GLTF2Mesh {
    private final String name;
    private final List<GLTF2Primitive> primitives;

    public GLTF2Mesh(String name) {
        this.name = name;
        this.primitives = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<GLTF2Primitive> getPrimitives() {
        return this.primitives;
    }
}
