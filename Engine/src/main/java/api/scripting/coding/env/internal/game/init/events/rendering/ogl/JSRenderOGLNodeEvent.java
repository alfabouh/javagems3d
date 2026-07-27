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

package api.scripting.coding.env.internal.game.init.events.rendering.ogl;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventCancellableI;
import api.scripting.coding.env.internal.util.events.JSEventRun;
import api.scripting.coding.env.internal.util.misc.JSFrameTicking;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;

@JSCodingClass(binding = "JSRenderOGLNodeEvent", description = "Event triggered when a render node is processed in OpenGL renderer.")
public class JSRenderOGLNodeEvent implements JSEventCancellableI {

    @JSCodingField(description = "Cancellation flag")
    private boolean cancel;

    @JSCodingField(description = "The OpenGL renderer")
    @JSHideFromDoc
    private JSOpenGLRenderer jsRenderer;

    @JSCodingField(description = "The render node being processed")
    @JSHideFromDoc
    private JSRenderNode jsRenderNode;

    @JSCodingField(description = "Frame timing data")
    @JSHideFromDoc
    private JSFrameTicking jsFrameTicking;

    @JSCodingField(description = "Event run state")
    @JSHideFromDoc
    private JSEventRun jsRun;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSRenderOGLNodeEvent() {
    }

    @JSHideFromDoc
    public JSRenderOGLNodeEvent(JSOpenGLRenderer jsRenderer, JSRenderNode jsRenderNode, JSFrameTicking jsFrameTicking, JSEventRun jsRun) {
        this.jsRenderer = jsRenderer;
        this.jsRenderNode = jsRenderNode;
        this.jsFrameTicking = jsFrameTicking;
        this.jsRun = jsRun;
    }

    @JSCodingFunctionOrMethod(description = "Get OpenGL renderer")
    public JSOpenGLRenderer getRenderer() {
        return this.jsRenderer;
    }

    @JSCodingFunctionOrMethod(description = "Get render node")
    public JSRenderNode getRenderNode() {
        return this.jsRenderNode;
    }

    @JSCodingFunctionOrMethod(description = "Get frame ticking data")
    public JSFrameTicking getFrameTicking() {
        return this.jsFrameTicking;
    }

    @JSCodingFunctionOrMethod(description = "Get run state")
    public JSEventRun getRun() {
        return this.jsRun;
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
        return "JSRenderOGLNodeEvent";
    }
}