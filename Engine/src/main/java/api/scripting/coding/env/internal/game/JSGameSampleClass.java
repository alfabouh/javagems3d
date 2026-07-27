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

package api.scripting.coding.env.internal.game;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSEntryPointSampleClass;
import api.scripting.coding.env.internal.game.init.JSGameRegistry;
import api.scripting.JavaToJsFunctionsList;

@JSEntryPointSampleClass
@JSCodingClass(binding = "entrypoint", description = "entrypoint")
public class JSGameSampleClass {

    @JSCodingFunctionOrMethod(description = JavaToJsFunctionsList.ENTRY_POINT_FUNCTION_DESC)
    public void JsInit() {
    }

    @JSCodingFunctionOrMethod(description = JavaToJsFunctionsList.SUBSCRIBE_EVENTS_FUNCTION_DESC, paramNames = {"jsGameRegistry"})
    public void JsSubscribeEvents(JSGameRegistry jsGameRegistry) {
    }

    @JSCodingFunctionOrMethod(description = JavaToJsFunctionsList.ENTRY_ENDPOINT_FUNCTION_DESC)
    public void JsEnd() {
    }
}
