package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.world.IRenderWorld;

import java.util.List;

public interface IParticlesManager {
    void update(IRenderWorld renderWorld);
    void spawnParticleFX(ParticleFX particleFX);
    void clear();
    List<ParticleFX> getParticlesFXCollection();
}
