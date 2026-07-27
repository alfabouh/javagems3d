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

package api.scripting.coding.env.internal.util.mapping.tags.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;
import javagems3d.system.external.mapping.tags.base.TranslationConstraints;

@JSCodingClass(binding = "JSTranslationConstraints", description = "Translation constraints wrapper.")
public class JSTranslationConstraints {

    @JSHideFromDoc
    private final TranslationConstraints constraints;

    @JSCodingConstructor(description = "Create translation constraints.", paramNames = {"position", "rotation", "scaling"})
    public JSTranslationConstraints(JSAxisConstraints pos, JSAxisConstraints rot, JSAxisConstraints scale) {
        this.constraints = new TranslationConstraints(pos.getJava(), rot.getJava(), scale.getJava());
    }

    @JSHideFromDoc
    public TranslationConstraints getJava() {
        return this.constraints;
    }

    @JSCodingFunctionOrMethod(description = "Get position constraints")
    public JSAxisConstraints getPosition() {
        return map(this.constraints.positionConstraints());
    }

    @JSCodingFunctionOrMethod(description = "Get rotation constraints")
    public JSAxisConstraints getRotation() {
        return map(this.constraints.rotationConstraints());
    }

    @JSCodingFunctionOrMethod(description = "Get scaling constraints")
    public JSAxisConstraints getScaling() {
        return map(this.constraints.scalingConstraints());
    }

    @JSHideFromDoc
    private JSAxisConstraints map(AxisConstraints raw) {
        for (JSAxisConstraints v : JSAxisConstraints.values()) {
            if (v.getJava() == raw) return v;
        }
        return JSAxisConstraints.NONE;
    }
}