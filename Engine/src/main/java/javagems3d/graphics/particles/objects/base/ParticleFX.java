package javagems3d.graphics.particles.objects.base;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.help.JGemsHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.JGems3D;
import javagems3d.graphics.particles.attributes.ParticleAttributes;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;

public abstract class ParticleFX implements IWorldObject {
    private final Vector3f position;
    private final Vector2f scaling;
    private final Vector3f colorMask;
    private final ParticleTexturesPack particleTexturesPack;
    private final SceneWorld world;
    private final JGemsTimedAction liveTimer;
    private final JGemsTimedAction frameTimer;
    private final ParticleAttributes particleAttributes;
    private boolean dead;
    private int currentFrame;

    public ParticleFX(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @Nullable ParticleTexturesPack particleTexturesPack, Vector3f pos, Vector2f scaling) {
        this.dead = false;
        this.position = new Vector3f(pos);
        this.scaling = new Vector2f(scaling);
        this.colorMask = new Vector3f(1.0f);

        this.particleAttributes = particleAttributes;
        this.world = world;
        this.particleTexturesPack = particleTexturesPack;

        this.liveTimer = JGemsHelper.screen().createTimer();

        if (particleTexturesPack != null) {
            this.frameTimer = JGemsHelper.screen().createTimer();
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
                this.currentFrame = JGems3D.random.nextInt(particleTexturesPack.getTexturesNum());
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

    public boolean hasTexturePack() {
        return this.getParticleTexturePack() != null;
    }

    public ITexture2DProgram getCurrentFrame() {
        return this.getParticleTexturePack().getTextureSamples()[this.currentFrame];
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

    public ParticleTexturesPack getParticleTexturePack() {
        return this.particleTexturesPack;
    }

    public SceneWorld getWorld() {
        return this.world;
    }

    public double getLivingTicks() {
        return this.liveTimer.getAccumulatedTime();
    }

    public boolean isDead() {
        return this.dead;
    }
}
