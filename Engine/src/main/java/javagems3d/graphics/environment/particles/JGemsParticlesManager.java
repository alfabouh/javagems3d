package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.data.material.ParticleFXSpriteProperties;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class JGemsParticlesManager extends ParticlesManager {
    public JGemsParticlesManager(IEnvironment environment) {
        super(environment);
    }

    @Override
    public ParticleEmitter createDefaultWorldParticleEmitter(@NotNull Vector3f position, @NotNull ImageTexture particleTexture, @NotNull ParticleFXSpriteProperties particleFXSpriteProperties, IEnvironment environment, float lifeTime) {
        return new ParticleEmitter(position, environment.getParticlesScene().getParticlesManager(), ParticleEmitter.DEFAULT_PARTICLE_WORLD(JGemsResourceManager.globalShaderAssets.world_particle_oit, JGemsResourceManager.globalShaderAssets.world_particle, particleTexture, particleFXSpriteProperties), lifeTime);
    }
}
