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

package api.scripting.coding.env.internal.game.init.events.rendering.ui;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;

@JSCodingClass(binding = "JSUiBehaviourTargets", description = "Enumeration of possible UI behaviour targets for panel callbacks.")
public enum JSUiBehaviourTargets {
    @JSCodingField(description = "Triggered when the UI panel is constructed.") ON_CONSTRUCT,
    @JSCodingField(description = "Triggered when the UI panel is destructed.") ON_DESTRUCT,
    @JSCodingField(description = "Triggered when the window containing the UI panel is resized.") ON_WINDOW_RESIZED,
    @JSCodingField(description = "Triggered every frame to draw the UI panel.") ON_DRAW
}
