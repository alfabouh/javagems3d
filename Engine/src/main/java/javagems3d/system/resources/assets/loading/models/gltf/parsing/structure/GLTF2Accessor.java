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

import javagems3d.system.service.exceptions.JGemsNullException;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GLTF2Accessor<T>(List<T> objects, GLTF2AccessorData gltf2AccessorData) {
    public GLTF2Accessor(List<T> objects, @Nullable GLTF2AccessorData gltf2AccessorData) {
        this.objects = objects;
        this.gltf2AccessorData = gltf2AccessorData;
    }

    public int size() {
        return this.objects().size();
    }

    public enum ValueType {
        SCALAR(1),
        VEC2(2),
        VEC3(3),
        VEC4(4),
        MAT2(4),
        MAT3(9),
        MAT4(16);

        public static int getTypeSize(String type) {
            switch (type) {
                case "SCALAR": {
                    return SCALAR.getCount();
                }
                case "VEC2": {
                    return VEC2.getCount();
                }
                case "VEC3": {
                    return VEC3.getCount();
                }
                case "VEC4": {
                    return VEC4.getCount();
                }
                case "MAT2": {
                    return MAT2.getCount();
                }
                case "MAT3": {
                    return MAT3.getCount();
                }
                case "MAT4": {
                    return MAT4.getCount();
                }
                default: {
                    throw new JGemsNullException("Couldn't create value: " + type);
                }
            }
        }

        private final int count;

        ValueType(int count) {
            this.count = count;
        }

        public int getCount() {
            return this.count;
        }
    }
}
