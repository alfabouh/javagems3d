package javagems3d.graphics.environment.particles.emitter;

import javagems3d.JGemsRandom;
import javagems3d.graphics.environment.particles.IParticlesManager;
import javagems3d.graphics.environment.particles.data.ParticleFXRenderConfig;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.environment.particles.fx.WorldDefaultParticleFX;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class ParticleEmitter implements IWorldObject, IWorldTicked {
    private Set<ParticleFX> linkedParticles;
    private boolean isDead;
    private JGemsTimedAction emitterLifeTime;
    private JGemsTimedAction particleRespawner;
    private final float lifeTime;
    private final Function<ParticleFXCreator, ParticleFX> particleCreator;
    private EmitterProperties emitterProperties;

    private SceneObject linkEmitterPosTo;

    private Vector3f emitterPosition;

    private float nextTimeToRespawn;
    private final IParticlesManager particlesManager;

    private boolean enabled;

    public ParticleEmitter(@NotNull Vector3f emitterPosition, @NotNull IParticlesManager particlesManager, @NotNull Function<ParticleFXCreator, ParticleFX> particleCreator, float lifeTime) {
        this.isDead = false;
        this.enabled = true;
        this.emitterProperties = new EmitterProperties();
        this.lifeTime = lifeTime;
        this.particleCreator = particleCreator;
        this.nextTimeToRespawn = 0.0f;
        this.particlesManager = particlesManager;
        this.emitterPosition = emitterPosition;
        this.linkedParticles = new HashSet<>();
        this.linkEmitterPosTo = null;
    }

    public static Function<ParticleFXCreator, ParticleFX> DEFAULT_PARTICLE_WORLD(@NotNull ParticleFXRenderConfig particleFXRenderConfig) {
        return (particleFXCreator -> new WorldDefaultParticleFX(particleFXRenderConfig.copy(), particleFXCreator.lifeTime(), particleFXCreator.gravity(), particleFXCreator.velocity(), particleFXCreator.acceleration())
                .setScaling(particleFXCreator.scaling())
                .setPosition(particleFXCreator.pos()));
    }

    protected ParticleFX createParticle(ParticleFXCreator particleCreator) {
        return this.particleCreator.apply(particleCreator);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        IRenderWorld renderWorld = (IRenderWorld) iWorld;
        this.emitterLifeTime = renderWorld.createTimer();
        this.particleRespawner = renderWorld.createTimer();
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        this.emitterLifeTime.dispose();
        this.particleRespawner.dispose();
        this.invalidate();
        this.linkedParticles = null;
    }

    @Override
    public void setDead() {
        this.isDead = true;
    }

    @Override
    public boolean isDead() {
        return this.isDead;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (!this.isEnabled()) {
            return;
        }
        if (this.getLinkEmitterPosTo() != null) {
            if (this.getLinkEmitterPosTo().isDead()) {
                this.invalidate();
                return;
            }
            if (this.getLinkEmitterPosTo().hasModel()) {
                this.setEmitterPosition(this.getLinkEmitterPosTo().getModel().getPose().getPosition());
            }
        }
        if (this.emitterLifeTime != null) {
            if (this.lifeTime > 0.0f && this.emitterLifeTime.resetTimerAfterReachedSeconds(this.lifeTime)) {
                this.spawnParticle();
                this.setDead();
            }
        }
        if (this.particleRespawner != null) {
            if (this.getEmitterProperties().particleRespawnTime > 0.0f) {
                if (this.particleRespawner.resetTimerAfterReachedSeconds(this.nextTimeToRespawn)) {
                    this.spawnParticle();
                    this.nextTimeToRespawn = this.getEmitterProperties().particleRespawnTime + (JGemsRandom.getRandom().nextFloat() * this.getEmitterProperties().particleRespawnTimeRandomOffsetRange);
                }
            } else {
                if (this.linkedParticles.size() > 1) {
                    this.setDead();
                } else if (this.linkedParticles.isEmpty()) {
                    this.spawnParticle();
                } else {
                    ParticleFX particleFX = this.linkedParticles.iterator().next();
                    if (particleFX.snapToEmitterPos()) {
                        particleFX.setPosition(new Vector3f(this.getEmitterPosition()).add(this.getEmitterProperties().getSpawnPosOffset()));
                    }
                }
            }
        }
        this.linkedParticles.removeIf(ParticleFX::isDead);
    }

    private void spawnParticle() {
        final ParticleFX createdParticle = this.createParticle(
                new ParticleFXCreator(
                        this.getEmitterProperties(),
                        new Vector3f(this.getEmitterPosition()).add(this.getEmitterProperties().getSpawnPosOffset()).add(JGemsRandom.instance.randomVector3f(this.getEmitterProperties().getParticleRandomSpawnPosOffsetRange())),
                        new Vector3f(this.getEmitterProperties().getParticleBaseScale(), 1.0f).add(new Vector3f(JGemsRandom.instance.randomVector2f(this.getEmitterProperties().getParticleRandomSpawnScalingOffsetRange()), 0.0f)),
                        this.getEmitterProperties().getParticleLifeTime() + (JGemsRandom.getRandom().nextFloat() * this.getEmitterProperties().getParticleLifeTimeRandomOffsetRange()),
                        new Vector3f(this.getEmitterProperties().getParticleGravity()),
                        new Vector3f(this.getEmitterProperties().getParticleBasicVelocity()).add(JGemsRandom.instance.randomVector3f(this.getEmitterProperties().getParticleBasicVelocityRandomOffsetRange())),
                        new Vector3f(this.getEmitterProperties().getParticleBasicAcceleration())
                )
        );
        this.particlesManager.spawnParticleFX(createdParticle);
        this.linkedParticles.add(createdParticle);
    }

    public boolean isEnabled() {
        return this.enabled && this.isAlive();
    }

    public ParticleEmitter setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Set<ParticleFX> getLinkedParticles() {
        return this.linkedParticles;
    }

    public void invalidate() {
        if (this.getEmitterProperties().isDestroyParticlesFromThatEmitterThenInvalidated()) {
            if (this.linkedParticles != null) {
                this.linkedParticles.forEach(ParticleFX::setDead);
                this.linkedParticles.clear();
            }
        }
    }

    public @Nullable SceneObject getLinkEmitterPosTo() {
        return this.linkEmitterPosTo;
    }

    public ParticleEmitter setLinkEmitterPosTo(@Nullable SceneObject linkEmitterPosTo) {
        this.linkEmitterPosTo = linkEmitterPosTo;
        return this;
    }

    public Vector3f getEmitterPosition() {
        return this.emitterPosition;
    }

    public ParticleEmitter setEmitterPosition(Vector3f emitterPosition) {
        this.emitterPosition = emitterPosition;
        return this;
    }

    public EmitterProperties getEmitterProperties() {
        return this.emitterProperties;
    }

    public ParticleEmitter setEmitterProperties(EmitterProperties emitterProperties) {
        this.emitterProperties = emitterProperties;
        return this;
    }

    public record ParticleFXCreator(EmitterProperties emitterProperties, Vector3f pos, Vector3f scaling, float lifeTime, Vector3f gravity, Vector3f velocity, Vector3f acceleration) {  }

    public static class EmitterProperties {
        private float particleRespawnTime;
        private float particleRespawnTimeRandomOffsetRange;

        private float particleLifeTime;
        private float particleLifeTimeRandomOffsetRange;

        private Vector3f spawnPosOffset;

        private Vector3f particleBasicVelocity;
        private Vector3f particleBasicVelocityRandomOffsetRange;
        private Vector3f particleBasicAcceleration;

        private Vector3f particleGravity;
        private Vector2f particleBaseScale;
        private Vector3f particleRandomSpawnPosOffsetRange;
        private float particleRandomSpawnScalingOffsetRange;

        private boolean destroyParticlesFromThatEmitterThenInvalidated;

        public EmitterProperties() {
            this.particleRespawnTime = 0.1f;
            this.particleRespawnTimeRandomOffsetRange = 0.0f;

            this.particleLifeTime = 3.0f;
            this.particleLifeTimeRandomOffsetRange = 0.0f;

            this.particleRandomSpawnPosOffsetRange = new Vector3f(0.0f, 0.0f, 0.0f);
            this.particleRandomSpawnScalingOffsetRange = 0.0f;

            this.destroyParticlesFromThatEmitterThenInvalidated = true;

            this.spawnPosOffset = new Vector3f(0.0f);

            this.particleBasicVelocity = new Vector3f(0.0f);
            this.particleBasicVelocityRandomOffsetRange = new Vector3f(0.0f);
            this.particleBasicAcceleration = new Vector3f(1.0f);

            this.particleGravity = new Vector3f(0.0f);
            this.particleBaseScale = new Vector2f(1.0f);
        }

        public boolean isDestroyParticlesFromThatEmitterThenInvalidated() {
            return this.destroyParticlesFromThatEmitterThenInvalidated;
        }

        public EmitterProperties setDestroyParticlesFromThatEmitterThenInvalidated(boolean destroyParticlesFromThatEmitterThenInvalidated) {
            this.destroyParticlesFromThatEmitterThenInvalidated = destroyParticlesFromThatEmitterThenInvalidated;
            return this;
        }

        public Vector3f getSpawnPosOffset() {
            return this.spawnPosOffset;
        }

        public EmitterProperties setSpawnPosOffset(Vector3f spawnPosOffset) {
            this.spawnPosOffset = spawnPosOffset;
            return this;
        }

        public Vector3f getParticleBasicVelocity() {
            return this.particleBasicVelocity;
        }

        public EmitterProperties setParticleBasicVelocity(Vector3f particleBasicVelocity) {
            this.particleBasicVelocity = particleBasicVelocity;
            return this;
        }

        public Vector3f getParticleBasicVelocityRandomOffsetRange() {
            return this.particleBasicVelocityRandomOffsetRange;
        }

        public EmitterProperties setParticleBasicVelocityRandomOffsetRange(Vector3f particleBasicVelocityRandomOffsetRange) {
            this.particleBasicVelocityRandomOffsetRange = particleBasicVelocityRandomOffsetRange;
            return this;
        }

        public Vector3f getParticleBasicAcceleration() {
            return this.particleBasicAcceleration;
        }

        public EmitterProperties setParticleBasicAcceleration(Vector3f particleBasicAcceleration) {
            this.particleBasicAcceleration = particleBasicAcceleration;
            return this;
        }

        public Vector3f getParticleGravity() {
            return this.particleGravity;
        }

        public EmitterProperties setParticleGravity(Vector3f particleGravity) {
            this.particleGravity = particleGravity;
            return this;
        }

        public Vector2f getParticleBaseScale() {
            return this.particleBaseScale;
        }

        public EmitterProperties setParticleBaseScale(Vector2f particleBaseScale) {
            this.particleBaseScale = particleBaseScale;
            return this;
        }

        public float getParticleRespawnTime() {
            return this.particleRespawnTime;
        }

        public EmitterProperties setParticleRespawnTime(float particleRespawnTime) {
            this.particleRespawnTime = particleRespawnTime;
            return this;
        }

        public float getParticleRespawnTimeRandomOffsetRange() {
            return this.particleRespawnTimeRandomOffsetRange;
        }

        public EmitterProperties setParticleRespawnTimeRandomOffsetRange(float particleRespawnTimeRandomOffsetRange) {
            this.particleRespawnTimeRandomOffsetRange = particleRespawnTimeRandomOffsetRange;
            return this;
        }

        public float getParticleLifeTime() {
            return this.particleLifeTime;
        }

        public EmitterProperties setParticleLifeTime(float particleLifeTime) {
            this.particleLifeTime = particleLifeTime;
            return this;
        }

        public float getParticleLifeTimeRandomOffsetRange() {
            return this.particleLifeTimeRandomOffsetRange;
        }

        public EmitterProperties setParticleLifeTimeRandomOffsetRange(float particleLifeTimeRandomOffsetRange) {
            this.particleLifeTimeRandomOffsetRange = particleLifeTimeRandomOffsetRange;
            return this;
        }

        public Vector3f getParticleRandomSpawnPosOffsetRange() {
            return this.particleRandomSpawnPosOffsetRange;
        }

        public EmitterProperties setParticleRandomSpawnPosOffsetRange(Vector3f particleRandomSpawnPosOffsetRange) {
            this.particleRandomSpawnPosOffsetRange = particleRandomSpawnPosOffsetRange;
            return this;
        }

        public float getParticleRandomSpawnScalingOffsetRange() {
            return this.particleRandomSpawnScalingOffsetRange;
        }

        public EmitterProperties setParticleRandomSpawnScalingOffsetRange(float particleRandomSpawnScalingOffsetRange) {
            this.particleRandomSpawnScalingOffsetRange = particleRandomSpawnScalingOffsetRange;
            return this;
        }
    }
}
