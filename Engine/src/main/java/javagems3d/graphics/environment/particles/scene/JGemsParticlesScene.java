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
