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
import api.scripting.coding.env.internal.util.settings.instances.JSSettingI;

@JSCodingClass(binding = "JSInitSettingsEvent", description = "Event triggered during game settings initialization, allows creating and registering settings.")
public class JSInitSettingsEvent implements JSEventI {
    @JSHideFromDoc private JSGameSettings jsGameSettings;

    @JSCodingConstructor(description = "Create a JSInitSettingsEvent with optional reference to JSGameSettings.")
    public JSInitSettingsEvent() {
    }

    @JSHideFromDoc
    public JSInitSettingsEvent(JSGameSettings jsGameSettings) {
        this.jsGameSettings = jsGameSettings;
    }

    @JSCodingFunctionOrMethod(description = "Register a new setting into the game settings system.", paramNames = {"setting"})
    public JSSettingI createSetting(JSSettingI setting) {
        return this.jsGameSettings.addSetting(setting);
    }

    @JSCodingFunctionOrMethod(description = "Get the game settings instance associated with this event.")
    public JSGameSettings getGameSettings() {
        return this.jsGameSettings;
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSInitSettingsEvent";
    }
}