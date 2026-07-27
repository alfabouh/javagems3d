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
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class JGemsParticlesManager extends ParticlesManager {
    public JGemsParticlesManager(IEnvironment environment) {
        super(environment);
    }

    @Override
    public ParticleEmitter createDefaultWorldParticleEmitter(@NotNull Vector3f position, @NotNull ParticleFXRenderConfig particleFXRenderConfig, float lifeTime) {
        return new ParticleEmitter(position, this.getEnvironment().getParticlesScene().getParticlesManager(), ParticleEmitter.DEFAULT_PARTICLE_WORLD(particleFXRenderConfig), lifeTime);
    }

    public static JGemsShaderManager DEFAULT_JGEMS_MAIN_SCENE_SHADER() {
        return JGemsResourceManager.globalShaderAssets.world_particle;
    }

    public static JGemsShaderManager DEFAULT_JGEMS_TRANSPARENCY_SCENE_SHADER() {
        return JGemsResourceManager.globalShaderAssets.world_particle_oit;
    }

    public JGemsShaderManager DEFAULT_MAIN_SCENE_SHADER() {
        return JGemsParticlesManager.DEFAULT_JGEMS_MAIN_SCENE_SHADER();
    }

    public JGemsShaderManager DEFAULT_TRANSPARENCY_SCENE_SHADER() {
        return JGemsParticlesManager.DEFAULT_JGEMS_TRANSPARENCY_SCENE_SHADER();
    }
}
