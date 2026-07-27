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

package api.scripting.coding.env.internal.game.init.events.physics;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;

@JSCodingClass(binding = "JSPhysicsWorldUpdateEvent", description = "Event triggered when the PhysicsWorld is updated (PRE or POST).")
public class JSPhysicsWorldUpdateEvent implements JSEventCancellableI {

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingField(description = "The physics world")
    @JSHideFromDoc
    private JSPhysicsWorld jsWorld;

    @JSCodingField(description = "Run phase of the update")
    @JSHideFromDoc
    private JSEventRun jsRun;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSPhysicsWorldUpdateEvent() {
    }

    @JSHideFromDoc
    public JSPhysicsWorldUpdateEvent(JSPhysicsWorld jsWorld, JSEventRun jsRun) {
        this.jsWorld = jsWorld;
        this.jsRun = jsRun;
    }

    @JSCodingFunctionOrMethod(description = "Get the physics world")
    public JSPhysicsWorld getWorld() {
        return this.jsWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the run phase of this update event")
    public JSEventRun getRun() {
        return this.jsRun;
    }

    @JSCodingFunctionOrMethod(description = "Check whether this event is cancelled.")
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
        return "JSPhysicsWorldUpdateEvent";
    }
}