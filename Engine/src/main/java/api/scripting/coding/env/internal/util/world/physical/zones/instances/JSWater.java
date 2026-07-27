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

package api.scripting.coding.env.internal.util.world.physical.zones.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.Water;

@JSCodingClass(binding = "JSWater", description = "Wrapper for Water liquid object")
public class JSWater implements JSWorldObjectI {
    @JSCodingField(description = "Real Water object")
    private final Water water;

    @JSHideFromDoc
    public JSWater(Water water) {
        this.water = water;
    }

    @JSHideFromDoc
    public JSWater(Zone zone) {
        this.water = new Water(zone);
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public Water getJavaWater() {
        return this.water;
    }


    @JSCodingFunctionOrMethod(description = "Real java object")
    @Override
    public IWorldObject getJavaWorldObject() {
        return this.water;
    }
}