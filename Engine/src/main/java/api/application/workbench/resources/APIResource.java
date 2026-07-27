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

package api.application.workbench.resources;

import javagems3d.system.service.files.VirtualObjectsFolder;
import org.jetbrains.annotations.NotNull;

public abstract class APIResource<T, E> implements VirtualObjectsFolder.ObjectWithName {
    private final MapObjectFabric<T> fabricWBench;
    private final MapObjectFabric<E> fabricGame;
    private final String name;

    public APIResource(@NotNull String name, @NotNull APIResource.MapObjectFabric<T> fabricWBench, @NotNull APIResource.MapObjectFabric<E> fabricGame) {
        this.name = name;
        this.fabricGame = fabricGame;
        this.fabricWBench = fabricWBench;
    }

    public MapObjectFabric<T> getFabricWBench() {
        return this.fabricWBench;
    }

    public MapObjectFabric<E> getFabricGame() {
        return this.fabricGame;
    }

    @Override
    public String name() {
        return this.name;
    }

    @FunctionalInterface
    public interface MapObjectFabric<Y> {
        Y create();
    }
}
