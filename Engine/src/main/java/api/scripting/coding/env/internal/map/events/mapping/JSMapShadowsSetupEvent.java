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

package api.scripting.coding.env.internal.map.events.mapping;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;

@JSCodingClass(binding = "JSMapShadowsSetupEvent", description = "Event triggered when shadows data is set up in the scene.")
public class JSMapShadowsSetupEvent implements JSEventCancellableI {
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSHideFromDoc
    private JSEnvironment jsEnvironment;

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.", paramNames = {})
    public JSMapShadowsSetupEvent() {
    }

    @JSHideFromDoc
    public JSMapShadowsSetupEvent(JSSceneWorld jsSceneWorld, JSEnvironment jsEnvironment) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsEnvironment = jsEnvironment;
    }

    @JSCodingFunctionOrMethod(description = "Get scene world associated with this event", paramNames = {})
    public JSSceneWorld getJsSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get scene environment associated with this event", paramNames = {})
    public JSEnvironment getJsEnvironment() {
        return this.jsEnvironment;
    }

    @JSHideFromDoc
    @Override
    public boolean isCancelled() { return this.cancel; }

    @JSHideFromDoc
    @Override
    public void setCancelled(boolean cancelled) { this.cancel = cancelled; }

    @JSHideFromDoc
    @Override
    public String name() { return "JSMapShadowsSetupEvent"; }
}