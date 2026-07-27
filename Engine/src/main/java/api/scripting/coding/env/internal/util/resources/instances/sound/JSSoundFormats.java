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

package api.scripting.coding.env.internal.util.resources.instances.sound;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;

@JSCodingClass(binding = "JSSoundFormats", description = "Audio format constants used in sound processing.")
public enum JSSoundFormats {
    @JSCodingField(description = "Mono, 8-bit") AL_FORMAT_MONO8(4352),
    @JSCodingField(description = "Mono, 16-bit") AL_FORMAT_MONO16(4353),
    @JSCodingField(description = "Stereo, 8-bit") AL_FORMAT_STEREO8(4354),
    @JSCodingField(description = "Stereo, 16-bit") AL_FORMAT_STEREO16(4355);

    @JSHideFromDoc
    private final int value;

    @JSHideFromDoc
    JSSoundFormats(int value) {
        this.value = value;
    }

    @JSHideFromDoc
    public int getValue() {
        return this.value;
    }

    @JSHideFromDoc
    @Override
    public String toString() {
        return super.toString();
    }
}