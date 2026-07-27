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

package api.scripting;

public abstract class JavaToJsFunctionsList {
    public static final String ENTRY_ENDPOINT_FUNCTION = "JsEnd";
    public static final String ENTRY_ENDPOINT_FUNCTION_DESC =
            "Entry point of the script. Called once when the script is ended";

    public static final String ENTRY_POINT_FUNCTION = "JsInit";
    public static final String ENTRY_POINT_FUNCTION_DESC =
            "Entry point of the script. Called once when the script is initialized. " +
                    "If this function is present, the file is treated as a script entry point.";

    public static final String SUBSCRIBE_EVENTS_FUNCTION = "JsSubscribeEvents";
    public static final String SUBSCRIBE_EVENTS_FUNCTION_DESC = "Function that subscribes to engine events.";
}
