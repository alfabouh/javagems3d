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

package api.scripting.coding.env.internal.util.resources.instances.textures;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSTextureCubeMapProperties", description = "Properties for cube map textures, e.g., linear filtration setting.")
public record JSTextureCubeMapProperties(boolean linearFiltration) {
    @JSCodingConstructor(description = "Constructor", paramNames = {"linearFiltration"})
    public JSTextureCubeMapProperties(boolean linearFiltration) {
        this.linearFiltration = linearFiltration;
    }

    @JSCodingConstructor(description = "DefaultPhysTest constructor")
    public JSTextureCubeMapProperties() {
        this(true);
    }

    @JSCodingFunctionOrMethod(description = "Check if linear filtration is enabled")
    @Override
    public boolean linearFiltration() {
        return this.linearFiltration;
    }

    @JSHideFromDoc
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @JSHideFromDoc
    @Override
    public int hashCode() {
        return 0;
    }

    @JSHideFromDoc
    @Override
    public @NotNull String toString() {
        return "";
    }
}