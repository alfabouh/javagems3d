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

package api.scripting.coding.env.internal.game.init.events.camera;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.world.render.processing.JSOpenGLRenderer;
import api.scripting.coding.env.internal.util.world.render.screen.camera.JSCamera;
import api.scripting.coding.env.internal.util.world.render.world.JSSceneWorld;
import api.scripting.coding.env.internal.util.world.render.world.environment.JSEnvironment;

@JSCodingClass(binding = "JSSceneCameraEvent", description = "Event triggered for camera updates in the scene.")
public class JSSceneCameraEvent implements JSEventI {
    @JSCodingField(description = "Scene world associated with the camera")
    @JSHideFromDoc
    private JSSceneWorld jsSceneWorld;

    @JSCodingField(description = "Camera involved in the event")
    @JSHideFromDoc
    private JSCamera jsCamera;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSSceneCameraEvent() {
    }

    @JSHideFromDoc
    public JSSceneCameraEvent(JSSceneWorld jsSceneWorld, JSCamera jsCamera) {
        this.jsSceneWorld = jsSceneWorld;
        this.jsCamera = jsCamera;
    }

    @JSCodingFunctionOrMethod(description = "Get the scene world associated with this event")
    public JSSceneWorld getSceneWorld() {
        return this.jsSceneWorld;
    }

    @JSCodingFunctionOrMethod(description = "Get the camera involved in this event")
    public JSCamera getCamera() {
        return this.jsCamera;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSSceneCameraEvent";
    }
}