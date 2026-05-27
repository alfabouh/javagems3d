package workbench.graphics.environment.components.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.ParticlesManager;
import javagems3d.graphics.environment.particles.data.ParticleFXRenderConfig;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.resources.WBenchResourceManager;

public class WBenchParticlesManager extends ParticlesManager {
    public WBenchParticlesManager(IEnvironment environment) {
        super(environment);
    }

    @Override
    public ParticleEmitter createDefaultWorldParticleEmitter(@NotNull Vector3f position, @NotNull ParticleFXRenderConfig particleFXRenderConfig, float lifeTime) {
        return new ParticleEmitter(position, this.getEnvironment().getParticlesScene().getParticlesManager(), ParticleEmitter.DEFAULT_PARTICLE_WORLD(particleFXRenderConfig), lifeTime);
    }

    public JGemsShaderManager DEFAULT_MAIN_SCENE_SHADER() {
        return WBenchResourceManager.localShaderAssets.world_particle;
    }

    public JGemsShaderManager DEFAULT_TRANSPARENCY_SCENE_SHADER() {
        return WBenchResourceManager.localShaderAssets.world_particle_oit;
    }
}
