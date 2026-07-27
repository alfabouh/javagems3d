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

package api.scripting.coding.env.internal.game.init.events.rendering.world;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;

@JSCodingClass(binding = "JSSceneObjectUpdateEvent", description = "Event triggered when a scene object is updated.")
public class JSSceneObjectUpdateEvent implements JSEventI {
    @JSCodingField(description = "Scene world associated with the object")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "The object being updated")
    @JSHideFromDoc
    private JSSceneObjectI jsObject;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneObjectUpdateEvent() {
    }

    @JSHideFromDoc
    public JSSceneObjectUpdateEvent(JSSceneWorld jsSceneWorld, JSSceneObjectI jsObject) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsObject = jsObject;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene object being updated")
    public JSSceneObjectI getObject() {
        return this.jsObject;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSSceneObjectUpdateEvent";
    }
}