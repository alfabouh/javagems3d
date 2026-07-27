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

package api.scripting.coding.env.internal.util.mapping.player;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;

@JSCodingClass(binding = "JSSpawnPlayerTranslateData", description = "Player spawn position and rotation")
public class JSSpawnPlayerTranslateData {
    @JSHideFromDoc
    private final JSVector3f spawnPos;
    @JSHideFromDoc
    private final JSVector3f spawnRot;

    @JSHideFromDoc
    public JSSpawnPlayerTranslateData(JSVector3f spawnPos, JSVector3f spawnRot) {
        this.spawnPos = spawnPos;
        this.spawnRot = spawnRot;
    }

    @JSCodingFunctionOrMethod(description = "Get spawn position")
    public JSVector3f getSpawnPos() { return this.spawnPos; }

    @JSCodingFunctionOrMethod(description = "Get spawn rotation")
    public JSVector3f getSpawnRot() { return this.spawnRot; }
}