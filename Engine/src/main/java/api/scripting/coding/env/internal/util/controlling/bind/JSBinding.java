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
import javagems3d.system.controller.binding.Binding;

@JSCodingClass(binding = "JSBinding", description = "Wrapper for a key binding with optional description.")
public class JSBinding {
    @JSHideFromDoc
    private final Binding binding;

    @JSCodingConstructor(description = "Creates a JSBinding for a specific JSKey with description.", paramNames = {"key", "description"})
    public JSBinding(JSKey key, String description) {
        this.binding = Binding.createBinding(key.getJavaKey(), description);
    }

    @JSHideFromDoc
    public JSBinding(Binding binding) {
        this.binding = binding;
    }

    @JSCodingFunctionOrMethod(description = "Sets the key for this binding.")
    public void setKey(JSKey key) {
        this.binding.setKeyToBinding(key.getJavaKey());
    }

    @JSCodingFunctionOrMethod(description = "Returns the JSKey associated with this binding.")
    public JSKey getKey() {
        return new JSKey(this.binding.getKey().getKeyCode());
    }

    @JSCodingFunctionOrMethod(description = "Returns the description of this binding.")
    public String getDescription() {
        return this.binding.getDescription();
    }

    @JSHideFromDoc
    public Binding getJavaBinding() {
        return this.binding;
    }

    @JSCodingFunctionOrMethod(description = "Returns a string representation like 'SPACE - Jump'.")
    public String toString() {
        return this.binding.toString();
    }
}