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

import javagems3d.graphics.environment.particles.IParticlesManager;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.particles.GroupedParticlesIndirectRenderer;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.Collection;
import java.util.function.Consumer;

public interface IParticlesScene {
    void createResources(OpenGLRenderer openGLRenderer);
    void destroyResources();

    void update(IRenderWorld renderWorld);

    GroupedParticlesIndirectRenderer getParticlesIndirectRendererScene();
    GroupedParticlesIndirectRenderer getParticlesIndirectRendererTransparency();
    Consumer<JGemsShaderManager> getDefaultConsumerForParticlesScene();

    default void passObjectInMainSceneSSBO(Collection<ParticleFX> filteredParticlesToRender) {
        this.getParticlesIndirectRendererScene().setIndirectMeshObjects(filteredParticlesToRender);
    }

    default void passObjectInTransparencySSBO() {
        this.getParticlesIndirectRendererTransparency().setIndirectMeshObjects(this.getParticlesIndirectRendererScene().getRejected());
    }

    IParticlesManager getParticlesManager();

    default void recreateResources(OpenGLRenderer openGLRenderer) {
        this.destroyResources();
        this.createResources(openGLRenderer);
    }
}
