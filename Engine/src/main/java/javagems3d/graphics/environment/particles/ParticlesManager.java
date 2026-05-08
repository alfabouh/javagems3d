package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.data.ParticleFXRenderData;
import javagems3d.graphics.environment.particles.data.material.ParticleFXMaterial;
import javagems3d.graphics.environment.particles.data.material.ParticleFXProperties;
import javagems3d.graphics.environment.particles.data.material.ParticleFXSpriteProperties;
import javagems3d.graphics.environment.particles.emitter.ParticleEmitter;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.environment.particles.fx.WorldDefaultParticleFX;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.controller.binding.DefaultBindings;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.*;

public class ParticlesManager implements IParticlesManager {
    private final IEnvironment environment;
    private final List<ParticleFX> particlesFXList;
    private final Set<ParticleEmitter> particleEmitters;

    public ParticlesManager(IEnvironment environment) {
        this.environment = environment;
        this.particleEmitters = new HashSet<>();
        this.particlesFXList = new ArrayList<>();
    }

    public static ParticleEmitter createDefaultParticleEmitter(Vector3f position, ImageTexture particleTexture, Vector2i cells, int maxSprites, IEnvironment environment, boolean fadeOutOnDeath, float lifeTime) {
        return new ParticleEmitter(position, environment.getParticlesScene().getParticlesManager(), ParticleEmitter.DEFAULT_PARTICLE_WORLD(particleTexture, cells, maxSprites, fadeOutOnDeath), lifeTime);
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
        if (((DefaultBindings) JGemsHelper.controller().getBindingManager()).keyX.isPressed()) {
            ParticleEmitter particleEmitter = ParticlesManager.createDefaultParticleEmitter(JGemsHelper.math().calcLookVector(renderWorld.getCamera().getCamRotation()).mul(5f).add(renderWorld.getCamera().getCamPosition()), JGemsResourceManager.globalTextureAssets.defaultParticle, new Vector2i(1, 1), 1, this.environment, true, -1.0f);
            particleEmitter.getEmitterProperties().setParticleGravity(new Vector3f(0.0f, 9.0f, 0.0f));
            particleEmitter.getEmitterProperties().setParticleLifeTime(3.0f);
            particleEmitter.getEmitterProperties().setParticleRandomSpawnPosOffsetRange(new Vector3f(3.0f));
            this.spawnParticleFXEmitter(particleEmitter);
        }
    }

    public void spawnParticleFX(ParticleFX particleFX) {
        particleFX.onSpawn(this.getEnvironment().getWorld());
        this.particlesFXList.add(particleFX);
        if (this.particlesFXList.size() > JGemsConfig.SYSTEM.MAX_PARTICLES) {
            ParticleFX particleFX1 = this.particlesFXList.removeFirst();
            particleFX1.setDead();
            particleFX1.onDestroy(this.getEnvironment().getWorld());
        }
    }

    public void spawnParticleFXEmitter(ParticleEmitter particleEmitter) {
        particleEmitter.onSpawn(this.getEnvironment().getWorld());
        this.particleEmitters.add(particleEmitter);
    }

    public void clear() {
        this.particlesFXList.forEach(e -> e.onDestroy(this.getEnvironment().getWorld()));
        this.particlesFXList.clear();

        this.particleEmitters.forEach(e -> e.onDestroy(this.getEnvironment().getWorld()));
        this.particleEmitters.clear();
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
