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
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.base.CameraBase;
import javagems3d.graphics.camera.base.ICamera;

@JSCodingClass(binding = "JSCameraBasic", description = "Base JS wrapper for any camera implementation.")
public class JSCameraBasic extends JSCamera {
    @JSHideFromDoc
    public JSCameraBasic(CameraBase camera) {
        super(camera);
    }

    @JSCodingFunctionOrMethod(description = "Sets camera position.")
    public void setCamPosition(JSVector3f pos) {
        this.getJavaCameraBase().setCameraPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Sets camera rotation.")
    public void setCamRotation(JSVector3f rot) {
        this.getJavaCameraBase().setCameraRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Moves camera by given offset.")
    public void addPosition(JSVector3f delta) {
        this.getJavaCameraBase().setCameraPosition(this.camera.getCamPosition().add(delta.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Rotates camera by given offset.")
    public void addRotation(JSVector3f delta) {
        this.getJavaCameraBase().setCameraRotation(this.camera.getCamRotation().add(delta.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Real java object.")
    public CameraBase getJavaCameraBase() {
        return (CameraBase) this.camera;
    }
}