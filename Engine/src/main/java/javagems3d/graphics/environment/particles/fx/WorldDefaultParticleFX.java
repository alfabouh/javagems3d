package javagems3d.graphics.environment.particles.fx;

import javagems3d.graphics.environment.particles.data.ParticleFXRenderConfig;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WorldDefaultParticleFX extends ParticleFX {
    private final float lifeTime;
    private Vector3f constantVelocity;
    private Vector3f constantAcceleration;
    private Vector3f gravity;
    private @Nullable JGemsTimedAction lifeTimer;
    private float interpolationAccum = 0.0f;

    public WorldDefaultParticleFX(@NotNull ParticleFXRenderConfig particleFXRenderConfig, float lifeTime) {
        this(particleFXRenderConfig, lifeTime, new Vector3f(0.0f, -10.0f, 0.0f), new Vector3f(), new Vector3f(1.0f));
    }


    public WorldDefaultParticleFX(@NotNull ParticleFXRenderConfig particleFXRenderConfig, float lifeTime, Vector3f constantVelocity) {
        this(particleFXRenderConfig, lifeTime, new Vector3f(0.0f, -10.0f, 0.0f), constantVelocity, new Vector3f(1.0f));
    }

    public WorldDefaultParticleFX(@NotNull ParticleFXRenderConfig particleFXRenderConfig, float lifeTime, Vector3f gravity, Vector3f constantVelocity) {
        this(particleFXRenderConfig, lifeTime, gravity, constantVelocity, new Vector3f(1.0f));
    }

    public WorldDefaultParticleFX(@NotNull ParticleFXRenderConfig particleFXRenderConfig, float lifeTime, Vector3f gravity, Vector3f constantVelocity, Vector3f constantAcceleration) {
        super(particleFXRenderConfig);
        this.lifeTime = lifeTime;
        this.constantVelocity = constantVelocity;
        this.constantAcceleration = constantAcceleration;
        this.gravity = gravity;
        this.interpolationAccum = 0.0f;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        IRenderWorld renderWorld = (IRenderWorld) iWorld;
        this.lifeTimer = renderWorld.createTimer();
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        if (this.lifeTimer != null) {
            this.lifeTimer.dispose();
        }
    }

    public float interpolationPoint() {
        return this.interpolationAccum;
    }

    private float frameDeltaLimitToUpdateSpriteID() {
        if (!this.getParticleFXRenderConfig().isLoop() && this.lifeTime > 0.0f) {
            return this.lifeTime / this.getParticleFXRenderConfig().getMaxSprites();
        }
        return this.getParticleFXRenderConfig().getLoopSpeed() / this.getParticleFXRenderConfig().getMaxSprites();
    }

    private void strengthAffected(IWorld world, float frameDelta) {
        Vector3f velocity = new Vector3f(this.constantVelocity).mul(this.constantAcceleration);
        this.setPosition(this.getPosition().add(velocity.mul(frameDelta)));
        this.constantVelocity.add(new Vector3f(this.gravity).mul(frameDelta));
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.lifeTimer != null) {
            if (this.lifeTime > 0.0f) {
                if (this.getParticleFXRenderConfig().isFadeOut()) {
                    final float f1 = this.lifeTime * 0.5f;
                    if (this.lifeTimer.getAccumulatedTime() >= f1) {
                        final float f2 = (float) (f1 / this.lifeTimer.getAccumulatedTime());
                        this.getParticleFXRenderConfig().getDiffuseColor().setColor(this.getParticleFXRenderConfig().getDiffuseColor().color().mul(1f, 1f, 1f, f2));
                        this.getParticleFXRenderConfig().setEmissionStrength(this.getParticleFXRenderConfig().getEmissionStrength() * f2);
                    }
                }
                if (this.lifeTimer.resetTimerAfterReachedSeconds(this.lifeTime)) {
                    this.setDead();
                }
            }
            this.interpolationAccum += this.lifeTimer.getDeltaTime() / this.frameDeltaLimitToUpdateSpriteID();
            if (this.interpolationAccum > 1.0f) {
                if (this.getCurrentTextureID() < this.getParticleFXRenderConfig().getMaxSprites() - 1) {
                    this.setCurrentTextureID(this.getCurrentTextureID() + 1);
                } else if (this.getParticleFXRenderConfig().isLoop()) {
                    this.setCurrentTextureID(0);
                }
                this.interpolationAccum %= 1.0f;
            }
            this.strengthAffected(iWorld, this.lifeTimer.getDeltaTime());
        }
    }

    @Override
    public int getInterpolateWithTextureID() {
        int nextFrame = this.getCurrentTextureID() + 1;
        if (nextFrame > this.getParticleFXRenderConfig().getMaxSprites() - 1) {
            if (this.getParticleFXRenderConfig().isLoop()) {
                nextFrame = 0;
            } else {
                nextFrame = this.getCurrentTextureID();
            }
        }
        return nextFrame;
    }

    @Override
    public boolean snapToEmitterPos() {
        return this.lifeTime <= 0.0f;
    }

    public Vector3f getGravity() {
        return this.gravity;
    }

    public WorldDefaultParticleFX setGravity(Vector3f gravity) {
        this.gravity = gravity;
        return this;
    }

    public Vector3f getConstantVelocity() {
        return this.constantVelocity;
    }

    public WorldDefaultParticleFX setConstantVelocity(@NotNull Vector3f constantVelocity) {
        this.constantVelocity = constantVelocity;
        return this;
    }

    public Vector3f getConstantAcceleration() {
        return this.constantAcceleration;
    }

    public WorldDefaultParticleFX setConstantAcceleration(@NotNull Vector3f constantAcceleration) {
        this.constantAcceleration = constantAcceleration;
        return this;
    }
}
