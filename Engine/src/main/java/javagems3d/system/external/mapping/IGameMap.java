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

package javagems3d.system.external.mapping;

import javagems3d.graphics.objects.rendering.data.EntityRenderData;
import javagems3d.physics.entities.kinematic.player.IPlayer;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;

public interface IGameMap {
    @NotNull String getName();
    @NotNull String getInformation();
    @Nullable IPlayer getCurrentPlayer();

    @FunctionalInterface
    interface IPlayerConstructor {
        Pair<@NotNull IPlayer, @Nullable EntityRenderData> constructPlayer(PhysicsWorld world, Collection<SpawnPlayerData> spawnPlayerData);
    }

    record SpawnPlayerData(Vector3f spawnPos, Vector3f spawnRot) {
        public static Collection<SpawnPlayerData> createSingle(Vector3f spawnPos, Vector3f spawnRot) {
            return List.of(new SpawnPlayerData(spawnPos, spawnRot));
        }
    }
}
