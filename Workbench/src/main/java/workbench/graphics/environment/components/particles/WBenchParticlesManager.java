package workbench.graphics.environment.components.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.ParticlesManager;
import javagems3d.graphics.environment.particles.data.material.ParticleFXSpriteProperties;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import workbench.resources.WBenchResourceManager;

public class WBenchParticlesManager extends ParticlesManager {
    public WBenchParticlesManager(IEnvironment environment) {
        super(environment);
    }

    @Override
    public ParticleEmitter createDefaultWorldParticleEmitter(@NotNull Vector3f position, @NotNull ImageTexture particleTexture, @NotNull ParticleFXSpriteProperties particleFXSpriteProperties, IEnvironment environment, float lifeTime) {
        return new ParticleEmitter(position, environment.getParticlesScene().getParticlesManager(), ParticleEmitter.DEFAULT_PARTICLE_WORLD(WBenchResourceManager.localShaderAssets.world_particle_oit, WBenchResourceManager.localShaderAssets.world_particle, particleTexture, particleFXSpriteProperties), lifeTime);
    }
}
