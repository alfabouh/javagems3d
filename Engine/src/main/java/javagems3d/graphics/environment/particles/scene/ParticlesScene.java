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
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.particles.GroupedParticlesIndirectRenderer;
import javagems3d.graphics.transformation.JGemsTransformManager;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.DefaultUniformDefinitions;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public abstract class ParticlesScene implements IParticlesScene {
    private final IEnvironment environment;
    private GroupedParticlesIndirectRenderer particlesIndirectRendererScene;
    private GroupedParticlesIndirectRenderer particlesIndirectRendererTransparency;
    private final IParticlesManager particlesManager;

    private final Consumer<JGemsShaderManager> defaultConsumerForParticlesScene;

    public ParticlesScene(@NotNull IEnvironment environment, @NotNull IParticlesManager particlesManager) {
        this.environment = environment;
        this.particlesManager = particlesManager;
        this.defaultConsumerForParticlesScene = (shaderManager) -> {
            final Matrix4f cameraMatrix = JGemsTransformManager.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformManager.INSTANCE.getPerspectiveMatrix();
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.PROJECTION_MATRIX), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString(DefaultUniformDefinitions.VIEW_MATRIX), UniformFunctions.MAT4F(cameraMatrix));
            JGemsHelper.render().performShadowsInfo(environment, shaderManager);
        };
    }

    protected abstract ShaderStorageBufferObject getParticlesIndirectSSBO();
    protected abstract ShaderStorageBufferObject getParticlesPropertiesSBO();

    @Override
    public void createResources(OpenGLRenderer openGLRenderer) {
        this.particlesIndirectRendererScene = new GroupedParticlesIndirectRenderer(openGLRenderer, IndirectRenderFabric.DEFAULT_FUNC, this.getParticlesIndirectSSBO(), this.getParticlesPropertiesSBO(), Pipeline.SOLID_SCENE);
        this.particlesIndirectRendererTransparency = new GroupedParticlesIndirectRenderer(openGLRenderer, IndirectRenderFabric.DEFAULT_FUNC, this.getParticlesIndirectSSBO(), this.getParticlesPropertiesSBO(), Pipeline.TRANSPARENCY);
    }

    @Override
    public void destroyResources() {
        this.particlesIndirectRendererScene = null;
        this.particlesIndirectRendererTransparency = null;
    }

    public Consumer<JGemsShaderManager> getDefaultConsumerForParticlesScene() {
        return this.defaultConsumerForParticlesScene;
    }

    public IParticlesManager getParticlesManager() {
        return this.particlesManager;
    }

    public GroupedParticlesIndirectRenderer getParticlesIndirectRendererScene() {
        return this.particlesIndirectRendererScene;
    }

    public GroupedParticlesIndirectRenderer getParticlesIndirectRendererTransparency() {
        return this.particlesIndirectRendererTransparency;
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }
}
