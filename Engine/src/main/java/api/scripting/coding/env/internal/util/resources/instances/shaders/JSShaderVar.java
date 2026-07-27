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

package api.scripting.coding.env.internal.util.resources.instances.shaders;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;

@JSCodingClass(binding = "JSShaderVar", description = "Wrapper for shader uniform variable.")
public class JSShaderVar {
    private final UniformString uniformString;

    @JSCodingConstructor(description = "Create shader uniform variable from name", paramNames = {"name"})
    public JSShaderVar(String name) {
        this.uniformString = new UniformString(name);
    }

    @JSCodingConstructor(description = "Create shader uniform variable with array index", paramNames = {"name", "index"})
    public JSShaderVar(String name, int index) {
        this.uniformString = new UniformString(name, index);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying UniformString instance")
    public UniformString getJavaUniformString() {
        return this.uniformString;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return this.uniformString.toString();
    }
}