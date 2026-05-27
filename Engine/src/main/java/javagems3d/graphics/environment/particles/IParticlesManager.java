package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.data.ParticleFXRenderConfig;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

public interface IParticlesManager {
    void update(IRenderWorld renderWorld);
    ParticleFX spawnParticleFX(@NotNull ParticleFX particleFX);
    ParticleEmitter spawnParticleFXEmitter(@NotNull ParticleEmitter particleEmitter);
    ParticleEmitter createDefaultWorldParticleEmitter(@NotNull Vector3f position, @NotNull ParticleFXRenderConfig particleFXRenderConfig, float lifeTime);

    default void destroyParticleFX(@NotNull ParticleFX particleFX) {
        particleFX.setDead();
    }
    default void destroyParticleEmitter(@NotNull ParticleEmitter particleEmitter) {
        particleEmitter.setDead();
    }

    JGemsShaderManager DEFAULT_MAIN_SCENE_SHADER();
    JGemsShaderManager DEFAULT_TRANSPARENCY_SCENE_SHADER();

    void clear();
    List<ParticleFX> getParticlesFXCollection();
}
