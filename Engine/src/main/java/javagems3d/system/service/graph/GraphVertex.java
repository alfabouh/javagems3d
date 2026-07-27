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

package javagems3d.system.service.graph;

import org.joml.Vector3f;

import java.io.Serializable;

public class GraphVertex implements Serializable {
    private static final long serialVersionUID = -228L;
    private final Vector3f position;
    private transient float g;
    private transient float h;
    private transient float f;
    private GraphVertex parent;

    public GraphVertex(Vector3f position) {
        this.position = position;
        this.g = 0;
        this.f = 0;
    }

    public double distanceTo(GraphVertex vertex) {
        return this.getPosition().distance(vertex.getPosition());
    }

    public double distanceTo(Vector3f vector3f) {
        return this.getPosition().distance(vector3f);
    }

    public synchronized float getH() {
        return this.h;
    }

    public synchronized void setH(float h) {
        this.h = h;
    }

    public synchronized float getG() {
        return this.g;
    }

    public synchronized void setG(float g) {
        this.g = g;
    }

    public synchronized double getF() {
        return this.f;
    }

    public synchronized void setF(float f) {
        this.f = f;
    }

    public synchronized GraphVertex getParent() {
        return this.parent;
    }

    public synchronized void setParent(GraphVertex parent) {
        this.parent = parent;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GraphVertex vertex)) {
            return false;
        }
        return vertex.toString().equals(this.toString());
    }

    public String toString() {
        return this.getPosition().toString();
    }
}
