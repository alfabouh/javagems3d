package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.data.material.ParticleFXSpriteProperties;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

public interface IParticlesManager {
    void update(IRenderWorld renderWorld);
    ParticleFX spawnParticleFX(@NotNull ParticleFX particleFX);
    ParticleEmitter spawnParticleFXEmitter(@NotNull ParticleEmitter particleEmitter);
    ParticleEmitter createDefaultWorldParticleEmitter(@NotNull Vector3f position, @NotNull ImageTexture particleTexture, @NotNull ParticleFXSpriteProperties particleFXSpriteProperties, IEnvironment environment, float lifeTime);

    default void destroyParticleFX(@NotNull ParticleFX particleFX) {
        particleFX.setDead();
    }

    default void destroyParticleEmitter(@NotNull ParticleEmitter particleEmitter) {
        particleEmitter.setDead();
    }

    void clear();
    List<ParticleFX> getParticlesFXCollection();
}
