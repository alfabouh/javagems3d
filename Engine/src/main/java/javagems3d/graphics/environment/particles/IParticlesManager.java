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

package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.data.ParticleFXRenderConfig;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

public interface IParticlesManager {
    void update(IRenderWorld renderWorld);
    ParticleFX spawnParticleFX(@NotNull ParticleFX particleFX);
    ParticleEmitter spawnParticleFXEmitter(@NotNull ParticleEmitter particleEmitter);
    ParticleEmitter createDefaultWorldParticleEmitter(@NotNull Vector3f position, @NotNull ParticleFXRenderConfig particleFXRenderConfig, float lifeTime);

    default void destroyParticleFX(@NotNull ParticleFX particleFX) {
        particleFX.setDead();
    }
    default void destroyParticleEmitter(@NotNull ParticleEmitter particleEmitter) {
        particleEmitter.setDead();
    }

    JGemsShaderManager DEFAULT_MAIN_SCENE_SHADER();
    JGemsShaderManager DEFAULT_TRANSPARENCY_SCENE_SHADER();

    void clear();
    List<ParticleFX> getParticlesFXCollection();
}
