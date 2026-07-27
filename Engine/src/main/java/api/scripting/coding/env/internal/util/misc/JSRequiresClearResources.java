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

package api.scripting.coding.env.internal.util.misc;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;

@JSCodingClass(binding = "JSRequiresClearResources", description = "Marks objects that hold resources requiring explicit cleanup. " +
        "Objects implementing this interface should call clear() after use to release memory or GPU resources, " +
        "especially if they are created each frame or frequently.")
public interface JSRequiresClearResources {
    @JSCodingFunctionOrMethod(description = "Releases all allocated resources held by the object. " +
            "Must be called when the object is no longer needed to prevent memory or GPU leaks.")
    void clear();
}