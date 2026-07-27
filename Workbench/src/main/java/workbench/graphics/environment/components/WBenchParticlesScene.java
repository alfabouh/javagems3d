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

package workbench.graphics.environment.components;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.IParticlesManager;
import javagems3d.graphics.environment.particles.scene.ParticlesScene;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import workbench.resources.WBenchResourceManager;

public class WBenchParticlesScene extends ParticlesScene {
    public WBenchParticlesScene(@NotNull IEnvironment environment, @NotNull IParticlesManager particlesManager) {
        super(environment, particlesManager);
    }

    @Override
    protected ShaderStorageBufferObject getParticlesIndirectSSBO() {
        return WBenchResourceManager.localShaderAssets.ParticleSceneIndirectBufferData;
    }

    @Override
    protected ShaderStorageBufferObject getParticlesPropertiesSBO() {
        return WBenchResourceManager.localShaderAssets.ParticleScenePropertiesData;
    }

    @Override
    public void update(IRenderWorld renderWorld) {
        this.getParticlesManager().update(renderWorld);
    }
}
