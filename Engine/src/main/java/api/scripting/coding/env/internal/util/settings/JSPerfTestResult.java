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

package api.scripting.coding.env.internal.util.settings;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.service.stat.PerformanceStat;

@JSCodingClass(binding = "JSPerfTestResult", description = "Performance test result levels.")
public enum JSPerfTestResult {
    @JSCodingField(description = "Very low performance") POTATO,
    @JSCodingField(description = "Low performance") LOW,
    @JSCodingField(description = "Medium performance") MEDIUM,
    @JSCodingField(description = "High performance") HIGH,
    @JSCodingField(description = "Excellent performance") GREAT;

    @JSHideFromDoc
    public static JSPerfTestResult choose(PerformanceStat.Result result) {
        return switch (result) {
            case POTATO -> POTATO;
            case LOW -> LOW;
            case MEDIUM -> MEDIUM;
            case HIGH -> HIGH;
            case GREAT -> GREAT;
        };
    }
}