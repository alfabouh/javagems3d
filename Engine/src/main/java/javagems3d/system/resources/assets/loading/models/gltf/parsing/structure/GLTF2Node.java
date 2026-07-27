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

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public final class GLTF2Node {
    private final int id;
    private final String name;
    private final GLTF2Mesh mesh;
    private GLTF2Node parent;
    private final List<GLTF2Node> children;
    private Matrix4f localTransform;
    private Matrix4f localAnimationTransform;
    private int skin;

    public GLTF2Node(int id, String name, @Nullable GLTF2Mesh mesh) {
        this.id = id;
        this.name = name;
        this.mesh = mesh;
        this.children = new ArrayList<>();
        this.parent = null;
        this.skin = -1;

        this.localAnimationTransform = null;
        this.localTransform = null;
    }

    public Matrix4f computeAnimationTransform(@Nullable Matrix4f inverseBindMatrix) {
        Matrix4f world = new Matrix4f().identity();
        GLTF2Node current = this;
        while (current != null) {
            Matrix4f local = (current.localAnimationTransform != null) ? current.localAnimationTransform : new Matrix4f().identity();
            world = new Matrix4f(local).mul(world);
            current = current.parent;
        }
        if (inverseBindMatrix != null) {
            world.mul(inverseBindMatrix, world);
        }
        return world;
    }

    public Matrix4f computeMeshTransform() {
        Matrix4f world = new Matrix4f().identity();
        GLTF2Node current = this;
        while (current != null) {
            Matrix4f localRest = (current.localTransform != null) ? current.localTransform : new Matrix4f().identity();
            world = new Matrix4f(localRest).mul(world);
            current = current.parent;
        }
        return world;
    }

    public Matrix4f getLocalAnimationTransform() {
        return this.localAnimationTransform;
    }

    public GLTF2Node setLocalAnimationTransform(Matrix4f localAnimationTransform) {
        this.localAnimationTransform = localAnimationTransform;
        return this;
    }

    public Matrix4f getLocalTransform() {
        return this.localTransform;
    }

    public GLTF2Node setLocalTransform(Matrix4f localTransform) {
        this.localTransform = localTransform;
        return this;
    }

    public GLTF2Node setSkin(int skin) {
        this.skin = skin;
        return this;
    }

    public GLTF2Node setParent(GLTF2Node parent) {
        this.parent = parent;
        return this;
    }

    public int getId() {
        return this.id;
    }

    public int getSkin() {
        return this.skin;
    }

    public String getName() {
        return this.name;
    }

    public GLTF2Node getParent() {
        return this.parent;
    }

    public List<GLTF2Node> getChildren() {
        return this.children;
    }

    public boolean hasMesh() {
        return this.getMesh() != null;
    }

    public GLTF2Mesh getMesh() {
        return this.mesh;
    }
}
