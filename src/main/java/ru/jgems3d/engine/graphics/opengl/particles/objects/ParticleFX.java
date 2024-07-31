package ru.jgems3d.engine.graphics.opengl.particles.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.frustum.ICulled;
import ru.jgems3d.engine.graphics.opengl.particles.attributes.ParticleAttributes;
import ru.jgems3d.engine.graphics.opengl.screen.timer.JGemsTimer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ParticleTexturePack;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;

public abstract class ParticleFX implements IWorldObject, ICulled {
    private boolean dead;
    private final Vector3f position;
    private final Vector2f scaling;
    private final ParticleTexturePack particleTexturePack;
    private final SceneWorld world;
    private final JGemsTimer liveTimer;
    private final JGemsTimer frameTimer;
    private final ParticleAttributes particleAttributes;
    private int currentFrame;

    public ParticleFX(SceneWorld world, @NotNull ParticleAttributes particleAttributes,  @NotNull ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
        this.dead = false;
        this.position = new Vector3f(pos);
        this.scaling = new Vector2f(scaling);

        this.particleAttributes = particleAttributes;
        this.world = world;
        this.particleTexturePack = particleTexturePack;

        this.liveTimer = JGemsHelper.createTimer();
        this.frameTimer = JGemsHelper.createTimer();
    }

    public void onUpdateParticle(double partialTicks, IWorld iWorld) {
        if (this.liveTimer.resetTimerAfterReachedSeconds(this.getMaxLivingSeconds())) {
            this.kill();
            return;
        }
        if (this.frameTimer.resetTimerAfterReachedSeconds(this.getParticleTexturePack().getAnimationRate())) {
            this.currentFrame += 1;
            if (this.currentFrame >= this.getParticleTexturePack().getTexturesNum()) {
                this.currentFrame = 0;
            }
        }
        this.updateParticle(partialTicks, iWorld);
    }

    public void onSpawn(IWorld iWorld) {
        this.liveTimer.reset();
        this.frameTimer.reset();
        if (this.getParticleTexturePack().getAnimationRate() <= 0.0f) {
            this.currentFrame = JGems3D.random.nextInt(particleTexturePack.getTexturesNum());
        }
    }

    public void onDestroy(IWorld iWorld) {
        this.liveTimer.dispose();
        this.frameTimer.dispose();
    }

    protected abstract void updateParticle(double partialTicks, IWorld world);

    public abstract double getMaxLivingSeconds();

    public void kill() {
        this.dead = true;
    }

    public ParticleFX setScaling(Vector2f scaling) {
        this.scaling.set(scaling);
        return this;
    }

    public ParticleFX setPosition(Vector3f pos) {
        this.position.set(pos);
        return this;
    }

    public RenderSphere calcRenderSphere() {
        float radius = (float) Math.sqrt(this.getScaling().x * this.getScaling().x + this.getScaling().y * this.getScaling().y);
        return new RenderSphere(radius, this.getPosition());
    }

    public IImageSample getCurrentFrame() {
        return this.getParticleTexturePack().getiImageSample()[this.currentFrame];
    }

    public ParticleAttributes getParticleAttributes() {
        return this.particleAttributes;
    }

    public Vector2f getScaling() {
        return new Vector2f(this.scaling);
    }

    public Vector3f getPosition() {
        return new Vector3f(this.position);
    }

    public ParticleTexturePack getParticleTexturePack() {
        return this.particleTexturePack;
    }

    public SceneWorld getWorld() {
        return this.world;
    }

    @Override
    public boolean canBeCulled() {
        return true;
    }

    public double getLivingTicks() {
        return this.liveTimer.getAccumulatedTime();
    }

    public boolean isDead() {
        return this.dead;
    }
}
