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
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectI;

@JSCodingClass(binding = "JSSceneObjectSpawnEvent", description = "Event triggered when a scene object is spawned, supports cancellation.")
public class JSSceneObjectSpawnEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "Scene world associated with the object")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "The object being spawned")
    @JSHideFromDoc
    private JSSceneObjectI jsObject;

    @JSCodingField(description = "Render data for the spawned object")
    @JSHideFromDoc
    private JSEntityRenderData jsRenderData;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneObjectSpawnEvent() {
    }

    @JSHideFromDoc
    public JSSceneObjectSpawnEvent(JSSceneWorld jsSceneWorld, JSSceneObjectI jsObject, JSEntityRenderData jsRenderData) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsObject = jsObject;
        this.jsRenderData = jsRenderData;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene object being spawned")
    public JSSceneObjectI getObject() {
        return this.jsObject;
    }

    @JSCodingFunctionOrMethod(description = "Get the render data for the spawned object")
    public JSEntityRenderData getRenderData() {
        return this.jsRenderData;
    }

    @JSCodingFunctionOrMethod(description = "Check whether this event is cancelled")
    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @JSCodingFunctionOrMethod(description = "Set event cancellation state. If true, default behavior will not execute.", paramNames = {"cancelled"})
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancel = cancelled;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSSceneObjectSpawnEvent";
    }
}