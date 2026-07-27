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

package api.scripting.coding.env.internal.game.init.events.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.settings.JSGameSettings;
import api.scripting.coding.env.internal.util.settings.JSPerfTestResult;
import api.scripting.coding.env.internal.util.settings.instances.JSSettingI;

@JSCodingClass(binding = "JSAfterSettingsPerfTestEvent", description = "Event triggered after settings performance test is completed, providing access to results and game settings.")
public class JSAfterSettingsPerfTestEvent implements JSEventI {
    @JSHideFromDoc private JSGameSettings jsGameSettings;
    @JSHideFromDoc private JSPerfTestResult jsPerfTestResult;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSAfterSettingsPerfTestEvent() {
    }

    @JSHideFromDoc
    public JSAfterSettingsPerfTestEvent(JSPerfTestResult jsPerfTestResult, JSGameSettings jsGameSettings) {
        this.jsPerfTestResult = jsPerfTestResult;
        this.jsGameSettings = jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "Get game settings instance.")
    public JSGameSettings getGameSettings() {
        return this.jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "Get performance test result.")
    public JSPerfTestResult getPerfTestResult() {
        return this.jsPerfTestResult;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSAfterSettingsPerfTestEvent";
    }
}