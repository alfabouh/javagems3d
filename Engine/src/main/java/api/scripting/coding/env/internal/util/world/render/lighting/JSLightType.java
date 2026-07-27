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

package api.scripting.coding.env.internal.util.world.render.lighting;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.graphics.environment.lights.LightType;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSLightType", description = "Wrapper for LightType enum. Allows checking and using light types.")
public class JSLightType {
    @JSCodingField(description = "Real java LightType object")
    private final LightType type;

    @JSCodingConstructor(description = "Wrap existing LightType enum value", paramNames = {"type"})
    public JSLightType(@NotNull LightType type) {
        this.type = type;
    }

    @JSCodingFunctionOrMethod(description = "Check if light type is POINT")
    public boolean isPoint() {
        return this.type == LightType.POINT;
    }

    @JSCodingFunctionOrMethod(description = "Check if light type is SUN")
    public boolean isSun() {
        return this.type == LightType.SUN;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying LightType object (for internal use)")
    public LightType getJavaLightType() {
        return this.type;
    }

    @JSCodingField(description = "POINT light type")
    public static final JSLightType POINT = new JSLightType(LightType.POINT);

    @JSCodingField(description = "SUN light type")
    public static final JSLightType SUN = new JSLightType(LightType.SUN);
}