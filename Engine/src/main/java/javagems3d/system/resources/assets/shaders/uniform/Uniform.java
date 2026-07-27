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

package javagems3d.system.resources.assets.shaders.uniform;

import java.util.HashSet;
import java.util.Set;

public class Uniform {
    private final String id;
    private final int arraySize;
    private final Set<String> fields;

    public Uniform(String id, int arraySize) {
        this.id = id;
        this.fields = new HashSet<>();
        this.arraySize = Math.max(arraySize, 1);
    }

    public Uniform(String id) {
        this(id, 1);
    }

    public Set<String> getFields() {
        return this.fields;
    }

    public String getId() {
        return this.id;
    }

    public int getArraySize() {
        return this.arraySize;
    }

    @Override
    public String toString() {
        return this.getId();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        return obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
