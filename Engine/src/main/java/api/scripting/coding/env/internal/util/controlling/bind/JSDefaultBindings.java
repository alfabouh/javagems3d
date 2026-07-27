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

package api.scripting.coding.env.internal.util.controlling.bind;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.bind.keys.JSKey;
import javagems3d.system.controller.binding.DefaultBindings;

@JSCodingClass(binding = "JSDefaultBindings", description = "DefaultPhysTest key bindings accessible from JS.")
public class JSDefaultBindings {
    @JSHideFromDoc
    private final DefaultBindings bindings;

    @JSCodingConstructor(description = "Creates default bindings.")
    public JSDefaultBindings() {
        this.bindings = new DefaultBindings();
    }

    @JSCodingFunctionOrMethod(description = "Returns key A (move left).")
    public JSKey getKeyA() { return new JSKey(this.bindings.keyA); }

    @JSCodingFunctionOrMethod(description = "Returns key D (move right).")
    public JSKey getKeyD() { return new JSKey(this.bindings.keyD); }

    @JSCodingFunctionOrMethod(description = "Returns key W (move forward).")
    public JSKey getKeyW() { return new JSKey(this.bindings.keyW); }

    @JSCodingFunctionOrMethod(description = "Returns key S (move backward).")
    public JSKey getKeyS() { return new JSKey(this.bindings.keyS); }

    @JSCodingFunctionOrMethod(description = "Returns jump key.")
    public JSKey getKeyUp() { return new JSKey(this.bindings.keyUp); }

    @JSCodingFunctionOrMethod(description = "Returns crouch key.")
    public JSKey getKeyDown() { return new JSKey(this.bindings.keyDown); }

    @JSCodingFunctionOrMethod(description = "Returns escape key.")
    public JSKey getKeyEsc() { return new JSKey(this.bindings.keyEsc); }

    @JSCodingFunctionOrMethod(description = "Returns selection key (mouse left).")
    public JSKey getKeySelection() { return new JSKey(this.bindings.keySelection); }

    @JSCodingFunctionOrMethod(description = "Returns binding used for moving left.")
    public JSKey keyMoveLeft() { return new JSKey(this.bindings.keyMoveLeft()); }

    @JSCodingFunctionOrMethod(description = "Returns binding used for moving right.")
    public JSKey keyMoveRight() { return new JSKey(this.bindings.keyMoveRight()); }

    @JSCodingFunctionOrMethod(description = "Returns binding used for moving forward.")
    public JSKey keyMoveForward() { return new JSKey(this.bindings.keyMoveForward()); }

    @JSCodingFunctionOrMethod(description = "Returns binding used for moving backward.")
    public JSKey keyMoveBackward() { return new JSKey(this.bindings.keyMoveBackward()); }

    @JSHideFromDoc
    public DefaultBindings getJavaBindings() {
        return this.bindings;
    }
}