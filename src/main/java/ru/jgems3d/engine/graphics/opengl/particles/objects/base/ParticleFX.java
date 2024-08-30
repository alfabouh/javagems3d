/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.graphics.opengl.particles.objects.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ITextureSample;
import ru.jgems3d.engine.system.resources.assets.material.samples.packs.ParticleTexturePack;

public abstract class ParticleFX implements IWorldObject, ICulled {
    private final Vector3f position;
    private final Vector2f scaling;
    private final Vector3f colorMask;
    private final ParticleTexturePack particleTexturePack;
    private final SceneWorld world;
    private final JGemsTimer liveTimer;
    private final JGemsTimer frameTimer;
    private final ParticleAttributes particleAttributes;
    private boolean dead;
    private int currentFrame;

    public ParticleFX(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @Nullable ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
        this.dead = false;
        this.position = new Vector3f(pos);
        this.scaling = new Vector2f(scaling);
        this.colorMask = new Vector3f(1.0f);

        this.particleAttributes = particleAttributes;
        this.world = world;
        this.particleTexturePack = particleTexturePack;

        this.liveTimer = JGemsHelper.createTimer();

        if (particleTexturePack != null) {
            this.frameTimer = JGemsHelper.createTimer();
        } else {
            this.frameTimer = null;
        }
    }

    public void onUpdateParticle(double frameDeltaTime, IWorld iWorld) {
        if (this.liveTimer.resetTimerAfterReachedSeconds(this.getMaxLivingSeconds())) {
            this.kill();
            return;
        }
        if (this.hasTexturePack()) {
            if (this.frameTimer.resetTimerAfterReachedSeconds(this.getParticleTexturePack().getAnimationRate())) {
                this.currentFrame += 1;
                if (this.currentFrame >= this.getParticleTexturePack().getTexturesNum()) {
                    this.currentFrame = 0;
                }
            }
        }
        this.updateParticle(frameDeltaTime, iWorld);
    }

    public void onSpawn(IWorld iWorld) {
        this.liveTimer.reset();
        if (this.hasTexturePack()) {
            this.frameTimer.reset();
            if (this.getParticleTexturePack().getAnimationRate() <= 0.0f) {
                this.currentFrame = JGems3D.random.nextInt(particleTexturePack.getTexturesNum());
            }
        }
    }

    public void onDestroy(IWorld iWorld) {
        this.liveTimer.dispose();
        if (this.hasTexturePack()) {
            this.frameTimer.dispose();
        }
    }

    protected abstract void updateParticle(double frameDeltaTime, IWorld world);

    public abstract double getMaxLivingSeconds();

    public void kill() {
        this.dead = true;
    }

    public RenderSphere calcRenderSphere() {
        float radius = (float) Math.sqrt(this.getScaling().x * this.getScaling().x + this.getScaling().y * this.getScaling().y);
        return new RenderSphere(radius, this.getPosition());
    }

    public boolean hasTexturePack() {
        return this.getParticleTexturePack() != null;
    }

    public ITextureSample getCurrentFrame() {
        return this.getParticleTexturePack().getiImageSample()[this.currentFrame];
    }

    public ParticleAttributes getParticleAttributes() {
        return this.particleAttributes;
    }

    public Vector3f getColorMask() {
        return new Vector3f(this.colorMask);
    }

    public ParticleFX setColorMask(Vector3f colorMask) {
        this.colorMask.set(colorMask);
        return this;
    }

    public Vector2f getScaling() {
        return new Vector2f(this.scaling);
    }

    public ParticleFX setScaling(Vector2f scaling) {
        this.scaling.set(scaling);
        return this;
    }

    public Vector3f getPosition() {
        return new Vector3f(this.position);
    }

    public ParticleFX setPosition(Vector3f pos) {
        this.position.set(pos);
        return this;
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
