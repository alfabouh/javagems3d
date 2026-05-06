package javagems3d.graphics.environment.particles.fx;

import javagems3d.graphics.environment.particles.data.ParticleFXRenderData;
import javagems3d.graphics.screen.timer.JGemsTimedAction;
import javagems3d.help.JGemsHelper;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WorldDefaultParticleFX extends ParticleFX {
    private float lifeTime;
    private Vector3f constantVelocity;
    private Vector3f constantAcceleration;
    private @Nullable JGemsTimedAction lifeTimer;
    private float interpolationAccum = 0.0f;

    public WorldDefaultParticleFX(@NotNull ParticleFXRenderData particleFXRenderData, float lifeTime, Vector3f constantVelocity, Vector3f constantAcceleration) {
        super(particleFXRenderData);
        this.lifeTime = lifeTime;
        this.constantVelocity = constantVelocity;
        this.constantAcceleration = constantAcceleration;
        this.interpolationAccum = 0.0f;
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        this.lifeTimer = JGemsHelper.screen().createTimer();
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
        if (!this.getParticleFXRenderData().spriteProperties().loop() && this.lifeTime > 0.0f) {
            return this.lifeTime / this.getParticleFXRenderData().spriteProperties().maxSprites();
        }
        return this.getParticleFXRenderData().spriteProperties().loopNextFrameInSecSpeed();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.lifeTimer != null) {
            if (this.lifeTime < 0.0f) {
                if (this.lifeTimer.resetTimerAfterReachedSeconds(this.lifeTime)) {
                    this.setDead();
                }
            }
            this.interpolationAccum += this.lifeTimer.getDeltaTime() / this.frameDeltaLimitToUpdateSpriteID();
            if (this.interpolationAccum > 1.0f) {
                if (this.getCurrentTextureID() < this.getParticleFXRenderData().spriteProperties().maxSprites() - 1) {
                    this.setCurrentTextureID(this.getCurrentTextureID() + 1);
                } else if (this.getParticleFXRenderData().spriteProperties().loop()) {
                    this.setCurrentTextureID(0);
                }
                this.interpolationAccum %= 1.0f;
            }
        }
    }

    @Override
    public int getInterpolateWithTextureID() {
        int nextFrame = this.getCurrentTextureID() + 1;
        if (nextFrame > this.getParticleFXRenderData().spriteProperties().maxSprites() - 1) {
            if (this.getParticleFXRenderData().spriteProperties().loop()) {
                nextFrame = 0;
            } else {
                nextFrame = this.getCurrentTextureID();
            }
        }
        return nextFrame;
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
