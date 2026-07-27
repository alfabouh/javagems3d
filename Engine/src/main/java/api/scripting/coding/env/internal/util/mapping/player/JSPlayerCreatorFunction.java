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
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.misc.JSPair;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.player.real.JSPlayer;
import api.scripting.coding.env.internal.util.world.render.data.JSEntityRenderData;
import javagems3d.physics.world.PhysicsWorld;

import java.util.Collection;

@JSCodingClass(binding = "JSPlayerCreator", description = "Function interface for creating a player in the game world. " + "Returns a pair of JSPlayer and optionally JSEntityRenderData (can be null if no render data needed).")
public interface JSPlayerCreatorFunction {
    @JSCodingFunctionOrMethod(description = "JSPair<JSPlayer, JSEntityRenderData> / Collection<JSSpawnPlayerTranslateData>. Create a player in the given JSPhysicsWorld. " + "The returned JSPair contains JSPlayer and optionally JSEntityRenderData. " + "If render data is not needed, the second value can be null.")
    JSPair<JSPlayer, JSEntityRenderData> createPlayer(JSPhysicsWorld world, Collection<JSSpawnPlayerTranslateData> spawnPlayerData);
}