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
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.external.mapping.tags.base.AxisConstraints;

@JSCodingClass(binding = "JSAxisConstraints", description = "Axis constraints enum with helpers.")
public enum JSAxisConstraints {

    @JSCodingField(description = "No axis")
    NONE(AxisConstraints.NONE),

    @JSCodingField(description = "X axis")
    AXIS_X(AxisConstraints.AXIS_X),

    @JSCodingField(description = "Y axis")
    AXIS_Y(AxisConstraints.AXIS_Y),

    @JSCodingField(description = "Z axis")
    AXIS_Z(AxisConstraints.AXIS_Z),

    @JSCodingField(description = "X and Y axis")
    AXIS_XY(AxisConstraints.AXIS_XY),

    @JSCodingField(description = "X and Z axis")
    AXIS_XZ(AxisConstraints.AXIS_XZ),

    @JSCodingField(description = "Y and Z axis")
    AXIS_YZ(AxisConstraints.AXIS_YZ),

    @JSCodingField(description = "All axis")
    AXIS_XYZ(AxisConstraints.AXIS_XYZ);

    @JSHideFromDoc
    private final AxisConstraints constraint;

    @JSHideFromDoc
    JSAxisConstraints(AxisConstraints constraint) {
        this.constraint = constraint;
    }

    @JSHideFromDoc
    public AxisConstraints getJava() {
        return this.constraint;
    }

    @JSCodingFunctionOrMethod(description = "Get flag value.", paramNames = {})
    public int getFlag() {
        return this.constraint.getFlag();
    }

    @JSCodingFunctionOrMethod(description = "Check if constraint exists in flag.", paramNames = {"flag", "constraint"})
    public static boolean check(int flag, JSAxisConstraints constraint) {
        return AxisConstraints.CHECK(flag, constraint.getJava());
    }

    @JSCodingFunctionOrMethod(description = "Create constraint from booleans.", paramNames = {"x", "y", "z"})
    public static JSAxisConstraints get(boolean x, boolean y, boolean z) {
        AxisConstraints raw = AxisConstraints.GET(x, y, z);
        for (JSAxisConstraints v : values()) {
            if (v.constraint == raw) return v;
        }
        return NONE;
    }
}