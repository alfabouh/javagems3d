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
