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

package api.scripting.coding.env.internal.util.controlling.bind.keys;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.controller.components.IKeyAction;

@JSCodingClass(binding = "JSKeyAction", description = "Represents key action callback.")
public class JSKeyAction {
    @JSHideFromDoc
    private final IKeyAction action;

    @JSCodingConstructor(description = "Creates key action.", paramNames = {"consumer"})
    public JSKeyAction(java.util.function.Consumer<String> consumer) {
        this.action = keyAction -> consumer.accept(keyAction.name());
    }

    @JSHideFromDoc
    public JSKeyAction(IKeyAction action) {
        this.action = action;
    }

    @JSHideFromDoc
    public void onTrigger(IKeyAction.KeyAction keyAction) {
        this.action.onTrigger(keyAction);
    }

    @JSHideFromDoc
    public IKeyAction getJavaAction() {
        return this.action;
    }
}