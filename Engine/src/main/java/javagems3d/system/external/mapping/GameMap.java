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

import javagems3d.physics.entities.kinematic.player.IPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class GameMap implements IGameMap {
    private final String name;
    private final String information;
    private final IPlayer player;

    public GameMap(@Nullable IPlayer player, String name, @Nullable String information) {
        this.name = name;
        this.information = information == null ? "***" : information;
        this.player = player;
    }

    @Override
    public @Nullable IPlayer getCurrentPlayer() {
        return this.player;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull String getInformation() {
        return this.information;
    }
}
