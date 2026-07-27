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

package javagems3d.graphics.environment.decals.scene;

import javagems3d.graphics.environment.decals.DecalMaterial;
import javagems3d.graphics.environment.decals.DecalTextureProperties;
import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.environment.decals.fx.WorldDefaultDecalFX;
import javagems3d.graphics.world.IRenderWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.util.List;

public interface IDecalsScene {
    void update(IRenderWorld renderWorld);
    DecalFX spawnDecalFX(@NotNull DecalFX decalFX);
    WorldDefaultDecalFX createDefaultWorldDecal(@NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Vector3f scale, @NotNull DecalMaterial decalMaterial, @NotNull DecalTextureProperties decalTextureProperties, float lifeTime, int terrainLayerID);

    default void destroyDecalFX(@NotNull DecalFX decalFX) {
        decalFX.setDead();
    }

    void clear();
    List<DecalFX> getDecalFXCollection();
}
