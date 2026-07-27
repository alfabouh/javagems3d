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

package api.scripting.coding.env.internal.util.lang;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.system.resources.localisation.LocalizationManager;

@JSCodingClass(binding = "JSLang", description = "Wrapper for localisation language.")
public class JSLang {

    private final LocalizationManager.Lang lang;

    public JSLang(LocalizationManager.Lang lang) {
        this.lang = lang;
    }

    @JSCodingFunctionOrMethod(description = "Get language name")
    public String getName() {
        return this.lang.lang();
    }

    public LocalizationManager.Lang getRaw() {
        return this.lang;
    }

    @Override
    public String toString() {
        return this.lang.lang();
    }
}