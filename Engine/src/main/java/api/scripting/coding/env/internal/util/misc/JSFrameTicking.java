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

package api.scripting.coding.env.internal.util.misc;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.screen.ticking.FrameTicking;

@JSCodingClass(binding = "JSFrameTicking", description = "Frame timing data: physics ticks and delta time.")
public class JSFrameTicking {

    @JSHideFromDoc
    private final FrameTicking ticking;

    @JSCodingConstructor(description = "Wrap existing FrameTicking.")
    public JSFrameTicking(FrameTicking ticking) {
        this.ticking = ticking;
    }

    @JSHideFromDoc
    public FrameTicking getJava() {
        return this.ticking;
    }

    @JSCodingFunctionOrMethod(description = "Get physics sync ticks (used for physics-synced movement interpolation).")
    public float getPhysicsSyncTicks() {
        return this.ticking.physicsSyncTicks();
    }

    @JSCodingFunctionOrMethod(description = "Get frame delta time (time between this and previous frame).")
    public float getFrameDeltaTime() {
        return this.ticking.frameDeltaTime();
    }

    @Override
    public String toString() {
        return "JSFrameTicking{" +
                "physicsSyncTicks=" + ticking.physicsSyncTicks() +
                ", frameDeltaTime=" + ticking.frameDeltaTime() +
                '}';
    }
}