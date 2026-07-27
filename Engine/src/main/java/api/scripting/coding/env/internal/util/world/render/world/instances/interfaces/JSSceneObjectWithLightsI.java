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

package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import javagems3d.graphics.environment.lights.ILightAttachable;
import javagems3d.graphics.objects.IObjectWithLights;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneObjectWithLightsI", description = "Interface for objects that support attaching and managing lights")
public interface JSSceneObjectWithLightsI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java lighted object", paramNames = {})
    IObjectWithLights getJavaLightedObject();

    @JSCodingFunctionOrMethod(description = "Attach light to this object", paramNames = {"light"})
    default void addLight(@NotNull JSLightI light) {
        this.getJavaLightedObject().addLightAttachment((ILightAttachable) light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Detach light from this object", paramNames = {"light"})
    default void removeLight(@NotNull JSLightI light) {
        this.getJavaLightedObject().removeLightAttachment((ILightAttachable) light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Check if light is attached", paramNames = {"light"})
    default boolean hasLight(@NotNull JSLightI light) {
        return this.getJavaLightedObject().isLightAttached((ILightAttachable) light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Check if object has any lights", paramNames = {})
    default boolean hasLights() {
        return this.getJavaLightedObject().hasLights();
    }

    @JSCodingFunctionOrMethod(description = "Get position used for attaching lights", paramNames = {})
    default JSVector3f getLightAttachPosition() {
        return new JSVector3f(this.getJavaLightedObject().getPositionToAttachLights());
    }
}