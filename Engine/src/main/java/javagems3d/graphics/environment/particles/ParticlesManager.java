package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.global.JGemsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class ParticlesManager implements IParticlesManager {
    private final IEnvironment environment;
    private final List<ParticleFX> particlesFXList;
    private final Set<ParticleEmitter> particleEmitters;

    public ParticlesManager(IEnvironment environment) {
        this.environment = environment;
        this.particleEmitters = new HashSet<>();
        this.particlesFXList = new ArrayList<>();
    }

    @Override
    public void update(IRenderWorld renderWorld) {
        {
            Iterator<ParticleEmitter> iterator = this.getParticleEmitters().iterator();
            while (iterator.hasNext()) {
                ParticleEmitter particleEmitter = iterator.next();
                if (particleEmitter.isDead()) {
                    particleEmitter.onDestroy(renderWorld);
                    iterator.remove();
                } else {
                    particleEmitter.onUpdate(renderWorld);
                }
            }
        }
        {
            Iterator<ParticleFX> iterator = this.getParticlesFXCollection().iterator();
            while (iterator.hasNext()) {
                ParticleFX particle = iterator.next();
                if (particle.isDead()) {
                    particle.onDestroy(renderWorld);
                    iterator.remove();
                } else {
                    particle.onUpdate(renderWorld);
                }
            }
        }
    }

    public ParticleFX spawnParticleFX(@NotNull ParticleFX particleFX) {
        particleFX.onSpawn(this.getEnvironment().getWorld());
        this.particlesFXList.add(particleFX);
        if (this.particlesFXList.size() > JGemsConfig.SYSTEM.MAX_PARTICLES) {
            ParticleFX particleFX1 = this.particlesFXList.removeFirst();
            particleFX1.setDead();
            particleFX1.onDestroy(this.getEnvironment().getWorld());
        }
        return particleFX;
    }

    public ParticleEmitter spawnParticleFXEmitter(@NotNull ParticleEmitter particleEmitter) {
        particleEmitter.onSpawn(this.getEnvironment().getWorld());
        this.particleEmitters.add(particleEmitter);
        return particleEmitter;
    }

    public void clear() {
        this.particleEmitters.forEach(e -> e.onDestroy(this.getEnvironment().getWorld()));
        this.particleEmitters.clear();

        this.particlesFXList.forEach(e -> e.onDestroy(this.getEnvironment().getWorld()));
        this.particlesFXList.clear();
    }

    public Set<ParticleEmitter> getParticleEmitters() {
        return this.particleEmitters;
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }

    public List<ParticleFX> getParticlesFXCollection() {
        return this.particlesFXList;
    }
}
