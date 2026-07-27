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

package api.scripting.coding.env.internal.util.world.render.data;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.JSMaterial;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import javagems3d.graphics.objects.rendering.data.LiquidRenderData;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSLiquidRenderData", description = "Wrapper for LiquidRenderData containing material and shader manager for liquids.")
public class JSLiquidRenderData {
    @JSHideFromDoc
    private final LiquidRenderData data;

    @JSCodingConstructor(description = "Create a new liquid render data", paramNames = {"material", "mainSceneShaderManager"})
    public JSLiquidRenderData(@NotNull JSMaterial material, @NotNull JSShader shaderManager) {
        this.data = new LiquidRenderData(material.getJavaMaterial(), shaderManager.getJavaShaderManager());
    }

    @JSHideFromDoc
    public JSLiquidRenderData(LiquidRenderData renderLiquidData) {
        this.data = renderLiquidData;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Material used for the liquid")
    public JSMaterial getLiquidMaterial() {
        return new JSMaterial(this.data.liquidMaterial());
    }

    @JSCodingFunctionOrMethod(description = "Returns the shader manager for this liquid")
    public JSShader getShaderManager() {
        return new JSShader(this.data.shaderManager());
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java LiquidRenderData object (unsafe, internal use)")
    public LiquidRenderData getJavaLiquidRenderData() {
        return this.data;
    }
}