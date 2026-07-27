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

package api.scripting.coding.env.internal.util.mapping;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.world.physical.player.real.JSPlayer;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.system.external.mapping.IGameMap;

@JSCodingClass(binding = "JSGameMap", description = "Wrapper for IGameMap interface, exposing map data and current player.")
public class JSGameMap {
    @JSHideFromDoc
    private final IGameMap gameMap;

    @JSHideFromDoc
    private JSPlayer currentJSPlayer;

    @JSHideFromDoc
    public JSGameMap(IGameMap gameMap) {
        this.gameMap = gameMap;
        IPlayer current = gameMap.getCurrentPlayer();
        this.currentJSPlayer = current != null ? () -> current : null;
    }

    @JSCodingFunctionOrMethod(description = "Get map name")
    public String getName() {
        return gameMap.getName();
    }

    @JSCodingFunctionOrMethod(description = "Get map information")
    public String getInformation() {
        return gameMap.getInformation();
    }

    @JSCodingFunctionOrMethod(description = "Get current player wrapper")
    public JSPlayer getCurrentPlayer() {
        IPlayer p = gameMap.getCurrentPlayer();
        if (p == null) return null;
        if (currentJSPlayer == null || currentJSPlayer.getJavaPlayer() != p) {
            currentJSPlayer = () -> p;
        }
        return currentJSPlayer;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java IGameMap object (unsafe)")
    @JSHideFromDoc
    public IGameMap getJavaGameMap() {
        return gameMap;
    }
}