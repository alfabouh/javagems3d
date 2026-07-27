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
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;

@JSCodingClass(binding = "JSInitRendererOGLEvent", description = "Event triggered when the OpenGL renderer is initialized.")
public class JSInitRendererOGLEvent implements JSEventI {
    @JSCodingField(description = "The OpenGL renderer instance")
    @JSHideFromDoc
    private JSOpenGLRenderer jsRenderer;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSInitRendererOGLEvent() {
    }

    @JSHideFromDoc
    public JSInitRendererOGLEvent(JSOpenGLRenderer jsRenderer) {
        this.jsRenderer = jsRenderer;
    }

    @JSCodingFunctionOrMethod(description = "Get the OpenGL renderer")
    public JSOpenGLRenderer getRenderer() {
        return this.jsRenderer;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSInitRendererOGLEvent";
    }
}