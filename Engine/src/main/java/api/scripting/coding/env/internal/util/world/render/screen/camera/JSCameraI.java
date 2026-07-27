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

package api.scripting.coding.env.internal.util.world.render.screen.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.base.ICamera;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSCameraI", description = "Interface for all camera types, providing access to position, rotation, and underlying Java camera object.")
public interface JSCameraI {
    @JSCodingFunctionOrMethod(description = "Get the current world position of the camera.") JSVector3f getCamPosition();
    @JSCodingFunctionOrMethod(description = "Get the current world rotation of the camera (Euler angles).") JSVector3f getCamRotation();
    @JSCodingFunctionOrMethod(description = "Get underlying Java camera object (for internal use, unsafe).") ICamera getJavaCamera();
}