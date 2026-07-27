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

package api.scripting.coding.env.internal.util.world.render.lighting;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import javagems3d.graphics.environment.lights.ILightAttachable;
import javagems3d.graphics.objects.IObjectWithLights;

@JSCodingClass(binding = "JSLightAttachableI", description = "Interface for lights that can be attached to scene objects")
public interface JSLightAttachableI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java light attachment object")
    ILightAttachable getJavaLightAttached();

    @JSCodingFunctionOrMethod(description = "Attach this light to scene object", paramNames = {"object"})
    default void attachTo(JSSceneObjectWithLightsI object) {
        this.getJavaLightAttached().attachTo(object == null ? null : object.getJavaLightedObject());
    }

    @JSCodingFunctionOrMethod(description = "Detach this light")
    default void detach() {
        this.getJavaLightAttached().detach();
    }

    @JSCodingFunctionOrMethod(description = "Get attached object")
    default JSSceneObjectWithLightsI getAttachedTo() {
        IObjectWithLights lighted = this.getJavaLightAttached().getAttachedTo();
        if (lighted == null) {
            return null;
        }
        return () -> lighted;
    }

    @JSCodingFunctionOrMethod(description = "Get action on detach")
    default JSActionOnDetach getActionOnDetach() {
        return JSActionOnDetach.valueOf(this.getJavaLightAttached().getActionOnDeath().name());
    }
}