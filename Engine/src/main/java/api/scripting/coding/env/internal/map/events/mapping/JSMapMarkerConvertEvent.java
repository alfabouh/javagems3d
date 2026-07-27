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

import api.events.EventBus;
import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.mapping.row.JSRowMapObjectData;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;

@JSCodingClass(binding = "JSMapMarkerConvertEvent", description = "Event triggered during conversion of a map marker to a scene marker instance.")
public class JSMapMarkerConvertEvent implements JSEventCancellableI {
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSHideFromDoc
    private JSPhysicsWorld jsPhysicsWorld;

    @JSHideFromDoc
    private JSRowMapObjectData jsTemplate;

    @JSCodingField(description = "Cancellation flag")
    @JSHideFromDoc
    private boolean cancel;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.", paramNames = {})
    public JSMapMarkerConvertEvent() {
    }

    @JSHideFromDoc
    public JSMapMarkerConvertEvent(JSSceneWorld jsSceneWorld, JSPhysicsWorld jsPhysicsWorld, JSRowMapObjectData jsTemplate) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsPhysicsWorld = jsPhysicsWorld;
        this.jsTemplate = jsTemplate;
    }

    @JSCodingFunctionOrMethod(description = "Get scene world associated with this event", paramNames = {})
    public JSSceneWorld getSceneWorld() { return this.jsSceneWorld; }

    @JSCodingFunctionOrMethod(description = "Get physics world associated with this event", paramNames = {})
    public JSPhysicsWorld getPhysicsWorld() { return this.jsPhysicsWorld; }

    @JSCodingFunctionOrMethod(description = "Get template data of the map object", paramNames = {})
    public JSRowMapObjectData getTemplate() { return this.jsTemplate; }

    @JSHideFromDoc
    @Override
    public boolean isCancelled() { return this.cancel; }

    @JSHideFromDoc
    @Override
    public void setCancelled(boolean cancelled) { this.cancel = cancelled; }

    @JSHideFromDoc
    @Override
    public String name() { return "JSMapMarkerConvertEvent"; }
}