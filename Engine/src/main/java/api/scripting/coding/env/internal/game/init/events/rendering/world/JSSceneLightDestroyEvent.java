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
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSSceneLightDestroyEvent", description = "Event triggered when a light in the scene is destroyed, supports cancellation.")
public class JSSceneLightDestroyEvent implements JSEventCancellableI {
    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "Scene world associated with the light")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "The light being destroyed")
    @JSHideFromDoc
    private JSLightI jsLight;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneLightDestroyEvent() {
    }

    @JSHideFromDoc
    public JSSceneLightDestroyEvent(JSSceneWorld jsSceneWorld, JSLightI jsLight) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsLight = jsLight;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the light being destroyed")
    public JSLightI getLight() {
        return this.jsLight;
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
        return "JSSceneLightDestroyEvent";
    }
}