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

package api.scripting.coding.env.internal.util.world.physical.zones.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.physics.world.triggers.Zone;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSZone", description = "Axis-aligned trigger zone defined by position and size.")
public class JSZone {

    @JSCodingField(description = "Zone center position")
    private final Vector3f location;

    @JSCodingField(description = "Zone size (width, height, depth)")
    private final Vector3f size;

    public JSZone(JSVector3f location, JSVector3f size) {
        this.location = location.getJavaVector3f();
        this.size = size.getJavaVector3f();
    }

    @JSCodingFunctionOrMethod(description = "Get location")
    public JSVector3f getLocation() {
        return new JSVector3f(new Vector3f(this.location));
    }

    @JSCodingFunctionOrMethod(description = "Get size")
    public JSVector3f getSize() {
        return new JSVector3f(new Vector3f(this.size));
    }

    @JSHideFromDoc
    public Zone getJavaZone() {
        return new Zone(this.location, this.size);
    }
}