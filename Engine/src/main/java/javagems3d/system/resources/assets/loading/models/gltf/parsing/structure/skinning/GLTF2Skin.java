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

package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.skinning;

import javagems3d.system.resources.assets.loading.models.gltf.parsing.structure.GLTF2Node;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.*;

public final class GLTF2Skin {
    private final String name;
    private final List<GLTF2Node> skeletonRoots;
    private final List<Integer> joints;
    private final Map<Integer, Integer> targetIdJointId;
    private final List<Matrix4f> inverseBindingMatrices;

    public GLTF2Skin(String name) {
        this.name = name;
        this.inverseBindingMatrices = new ArrayList<>();
        this.targetIdJointId = new HashMap<>();
        this.skeletonRoots = new ArrayList<>();
        this.joints = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<Integer> getJoints() {
        return this.joints;
    }

    public List<GLTF2Node> getSkeletonRoots() {
        return this.skeletonRoots;
    }

    public Map<Integer, Integer> getTargetIdJointId() {
        return this.targetIdJointId;
    }

    public List<Matrix4f> getInverseBindingMatrices() {
        return this.inverseBindingMatrices;
    }
}
