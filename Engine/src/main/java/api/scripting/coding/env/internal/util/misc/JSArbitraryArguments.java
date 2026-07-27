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

package api.scripting.coding.env.internal.util.misc;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.service.args.ArbitraryArguments;

@JSCodingClass(binding = "JSArbitraryArguments", description = "Container for passing arbitrary arguments to render functions.")
public class JSArbitraryArguments {
    private final ArbitraryArguments args;

    @JSHideFromDoc
    public JSArbitraryArguments(ArbitraryArguments args) {
        this.args = args;
    }

    @JSCodingFunctionOrMethod(description = "Create empty arguments")
    public static JSArbitraryArguments empty() {
        return new JSArbitraryArguments(ArbitraryArguments.empty());
    }

    @JSCodingFunctionOrMethod(description = "Create arguments with arbitrary objects")
    public static JSArbitraryArguments pass(Object... objects) {
        return new JSArbitraryArguments(ArbitraryArguments.pass(objects));
    }

    @JSCodingFunctionOrMethod(description = "Get the object at the specified index")
    public Object get(int index) {
        return args.getObjects()[index];
    }

    @JSCodingFunctionOrMethod(description = "Check if arguments length matches expected")
    public boolean checkLength(int expected) {
        return args.getterFunc().checkLength(expected);
    }

    @JSCodingFunctionOrMethod(description = "Check if argument types match given classes")
    public boolean checkRowByTypes(Class<?>... types) {
        return args.getterFunc().checkRowByTypes(types);
    }

    @JSCodingFunctionOrMethod(description = "Get underlying ArbitraryArguments object")
    public ArbitraryArguments getJavaArgs() {
        return this.args;
    }
}