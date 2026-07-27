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

package api.scripting.coding.env.internal.util.world.render.processing;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;

@JSCodingClass(binding = "JSCullingRules", description = "Wrapper for CullingRules. Provides control over distance and frustum culling for scene objects.")
public class JSCullingRules {

    @JSCodingField(description = "Underlying Java CullingRules object")
    private final CullingRules rules;

    @JSCodingConstructor(description = "Create a new default CullingRules instance")
    public JSCullingRules() {
        this.rules = new CullingRules();
    }

    @JSCodingConstructor(description = "Wrap an existing CullingRules instance")
    public JSCullingRules(CullingRules rules) {
        this.rules = rules;
    }

    @JSCodingFunctionOrMethod(description = "Check if distance culling is ignored")
    public boolean isIgnoreDistanceCulling() {
        return rules.isIgnoreDistanceCulling();
    }

    @JSCodingFunctionOrMethod(description = "Enable or disable ignoring distance culling")
    public void setIgnoreDistanceCulling(boolean ignore) {
        rules.setIgnoreDistanceCulling(ignore);
    }

    @JSCodingFunctionOrMethod(description = "Check if frustum culling is ignored")
    public boolean isIgnoreFrustumCulling() {
        return rules.isIgnoreFrustumCulling();
    }

    @JSCodingFunctionOrMethod(description = "Enable or disable ignoring frustum culling")
    public void setIgnoreFrustumCulling(boolean ignore) {
        rules.setIgnoreFrustumCulling(ignore);
    }

    @JSCodingFunctionOrMethod(description = "Create a copy of these culling rules")
    public JSCullingRules copy() {
        return new JSCullingRules(rules.copy());
    }

    @JSCodingFunctionOrMethod(description = "Get the underlying CullingRules object (for internal use)")
    public CullingRules getJavaCullingRules() {
        return this.rules;
    }

    @JSCodingField(description = "DefaultPhysTest CullingRules instance")
    public static final JSCullingRules DEFAULT = new JSCullingRules(CullingRules.get());
}