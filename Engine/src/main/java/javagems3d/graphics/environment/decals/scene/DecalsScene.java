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

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.environment.lights.scene.LightScene;
import javagems3d.graphics.rendering.programs.ssbo.ShaderStorageBufferProgram;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public abstract class DecalsScene implements IDecalsScene {
    private final List<DecalFX> decalFXList;
    private final IEnvironment environment;

    public DecalsScene(IEnvironment environment) {
        this.decalFXList = new ArrayList<>();
        this.environment = environment;
    }

    @Override
    public void update(IRenderWorld renderWorld) {
        {
            Iterator<DecalFX> iterator = this.getDecalFXCollection().iterator();
            while (iterator.hasNext()) {
                DecalFX decalFX = iterator.next();
                if (decalFX.isDead()) {
                    decalFX.onDestroy(renderWorld);
                    iterator.remove();
                } else {
                    decalFX.onUpdate(renderWorld);
                }
            }
        }
    }

    @Override
    public DecalFX spawnDecalFX(@NotNull DecalFX decalFX) {
        this.decalFXList.add(decalFX);
        if (this.decalFXList.size() > JGemsConfig.SYSTEM.MAX_DECALS) {
            Optional<DecalFX> optionalD = this.decalFXList.stream().filter(e -> !e.unDestructible()).findFirst();
            optionalD.ifPresent(IWorldObject::setDead);
        }
        decalFX.onSpawn(this.getEnvironment().getWorld());
        return decalFX;
    }

    @Override
    public void clear() {
        this.decalFXList.forEach(e -> e.onDestroy(this.getEnvironment().getWorld()));
        this.decalFXList.clear();
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }

    @Override
    public List<DecalFX> getDecalFXCollection() {
        return this.decalFXList;
    }
}
