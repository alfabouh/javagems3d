package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.world.IRenderWorld;

import java.util.List;

public interface IParticlesManager {
    void update(IRenderWorld renderWorld);
    void spawnParticleFX(ParticleFX particleFX);
    List<ParticleFX> getParticlesFXList();
}
