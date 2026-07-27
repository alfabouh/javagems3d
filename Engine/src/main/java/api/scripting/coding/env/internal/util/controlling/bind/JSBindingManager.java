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
import javagems3d.system.controller.binding.BindingManager;

@JSCodingClass(binding = "JSBindingManager", description = "Manages a set of key bindings and provides access to movement keys.")
public class JSBindingManager {
    @JSHideFromDoc
    protected final BindingManager manager;

    @JSCodingConstructor(description = "Wraps an existing BindingManager.", paramNames = {"manager"})
    public JSBindingManager(BindingManager manager) {
        this.manager = manager;
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move left.")
    public JSKey keyMoveLeft() {
        return new JSKey(this.manager.keyMoveLeft());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move right.")
    public JSKey keyMoveRight(){
        return new JSKey(this.manager.keyMoveRight());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move forward.")
    public JSKey keyMoveForward(){
        return new JSKey(this.manager.keyMoveForward());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move backward.")
    public JSKey keyMoveBackward(){
        return new JSKey(this.manager.keyMoveBackward());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move up.")
    public JSKey keyMoveUp(){
        return new JSKey(this.manager.keyMoveUp());
    }

    @JSCodingFunctionOrMethod(description = "Returns the key used to move down.")
    public JSKey keyMoveDown(){
        return new JSKey(this.manager.keyMoveDown());
    }

    @JSCodingFunctionOrMethod(description = "Adds a new binding.")
    public void addBinding(JSBinding binding) {
        this.manager.addBinding(binding.getJavaBinding());
    }

    @JSCodingFunctionOrMethod(description = "Removes a binding by JSKey.")
    public void removeBinding(JSKey key) {
        this.manager.removeBinding(key.getJavaKey());
    }

    @JSCodingFunctionOrMethod(description = "Returns all bindings managed by this manager.")
    public JSBinding[] getBindings() {
        return this.manager.getBindingSet().stream().map(JSBinding::new).toArray(JSBinding[]::new);
    }

    @JSHideFromDoc
    public BindingManager getJavaManager() {
        return this.manager;
    }
}