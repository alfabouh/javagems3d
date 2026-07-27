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

package javagems3d.graphics.environment.particles.scene;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.IParticlesManager;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

public class JGemsParticlesScene extends ParticlesScene {
    public JGemsParticlesScene(@NotNull IEnvironment environment, @NotNull IParticlesManager particlesManager) {
        super(environment, particlesManager);
    }

    @Override
    protected ShaderStorageBufferObject getParticlesIndirectSSBO() {
        return JGemsResourceManager.globalShaderAssets.ParticleSceneIndirectBufferData;
    }

    @Override
    protected ShaderStorageBufferObject getParticlesPropertiesSBO() {
        return JGemsResourceManager.globalShaderAssets.ParticleScenePropertiesData;
    }

    @Override
    public void update(IRenderWorld renderWorld) {
        this.getParticlesManager().update(renderWorld);
    }
}
