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

@JSCodingClass(binding = "JSTexture2DProperties", description = "Wrapper for 2D texture settings, including mipmapping, filtration, tiling, anisotropic filtering, and quality influence.")
public record JSTexture2DProperties(boolean mipMap, boolean linearFiltration, boolean shouldBeRepeated, boolean anisotropicFiltration, boolean qualityAffected) {
    @JSCodingConstructor(description = "Full constructor with all texture properties.", paramNames = {"mipMap", "linearFiltration", "shouldBeRepeated", "anisotropicFiltration", "qualityAffected"})
    public JSTexture2DProperties(boolean mipMap, boolean linearFiltration, boolean shouldBeRepeated, boolean anisotropicFiltration, boolean qualityAffected) {
        this.mipMap = mipMap;
        this.linearFiltration = linearFiltration;
        this.shouldBeRepeated = shouldBeRepeated;
        this.anisotropicFiltration = anisotropicFiltration;
        this.qualityAffected = qualityAffected;
    }

    @JSCodingConstructor(description = "Simplified constructor with mipMap and qualityAffected only.", paramNames = {"mipMap", "qualityAffected"})
    public JSTexture2DProperties(boolean mipMap, boolean qualityAffected) {
        this(mipMap, true, true, true, qualityAffected);
    }

    public JSTexture2DProperties() {
        this(true, true, true, true, false);
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

    @JSCodingFunctionOrMethod(description = "Whether mipmaps are enabled for this texture.")
    @Override
    public boolean mipMap() {
        return this.mipMap;
    }

    @JSCodingFunctionOrMethod(description = "Whether linear filtering is enabled for this texture.")
    @Override
    public boolean linearFiltration() {
        return this.linearFiltration;
    }

    @JSCodingFunctionOrMethod(description = "Whether the texture should be repeated when UVs exceed 1.0.")
    @Override
    public boolean shouldBeRepeated() {
        return this.shouldBeRepeated;
    }

    @JSCodingFunctionOrMethod(description = "Whether anisotropic filtering is enabled for this texture.")
    @Override
    public boolean anisotropicFiltration() {
        return this.anisotropicFiltration;
    }

    @JSCodingFunctionOrMethod(description = "Whether this texture affects quality-dependent rendering settings.")
    @Override
    public boolean qualityAffected() {
        return this.qualityAffected;
    }
}