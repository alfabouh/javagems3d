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

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Animations;
import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning.GLTF2Skin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GLTF2Scene(String name, List<GLTF2Material> materials, List<GLTF2Skin> skins,
                         List<GLTF2Animations> animations, List<GLTF2Node> nodes) {
    public GLTF2Scene(String name, List<GLTF2Material> materials, @Nullable List<GLTF2Skin> skins, @Nullable List<GLTF2Animations> animations, List<GLTF2Node> nodes) {
        this.name = name;
        this.materials = materials;
        this.skins = skins;
        this.animations = animations;
        this.nodes = nodes;
    }
}