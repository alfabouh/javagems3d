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
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.function.Consumer;

public abstract class ParticlesScene implements IParticlesScene {
    private final IEnvironment environment;
    private GroupedParticlesIndirectRenderer particlesIndirectRendererScene;
    private GroupedParticlesIndirectRenderer particlesIndirectRendererTransparency;
    private final IParticlesManager particlesManager;

    private Consumer<JGemsShaderManager> defaultConsumerForParticlesScene;

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
        this.particlesIndirectRendererScene = new GroupedParticlesIndirectRenderer(openGLRenderer, IndirectRenderFabric.DEFAULT_FUNC, this.getParticlesIndirectSSBO(), this.getParticlesPropertiesSBO(), Pipeline.SCENE);
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
