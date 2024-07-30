package ru.jgems3d.engine.graphics.opengl.particles.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.frustum.ICulled;
import ru.jgems3d.engine.graphics.opengl.screen.timer.GameRenderTimer;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ParticleTexturePack;

public abstract class ParticleFX implements IWorldObject, ICulled {
    private boolean dead;
    private final Vector3f position;
    private final Vector2f scaling;
    private final ParticleTexturePack particleTexturePack;
    private final SceneWorld world;
    private final GameRenderTimer gameRenderTimer;

    public ParticleFX(SceneWorld world, @NotNull ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
        this.dead = false;
        this.position = new Vector3f(pos);
        this.scaling = new Vector2f(scaling);

        this.world = world;
        this.particleTexturePack = particleTexturePack;
        this.gameRenderTimer = new GameRenderTimer();
    }

    public void onUpdateParticle(double partialTicks, IWorld iWorld) {
        if (this.gameRenderTimer.resetTimerAfterReachedSeconds(this.getMaxLivingTime())) {
            this.kill();
        }
        this.updateParticle(partialTicks, iWorld);
    }

    public void onSpawn(IWorld iWorld) {
        this.gameRenderTimer.reset();
    }

    public void onDestroy(IWorld iWorld) {
        this.gameRenderTimer.dispose();
    }

    protected abstract void updateParticle(double partialTicks, IWorld world);

    public abstract double getMaxLivingTime();

    public void kill() {
        this.dead = true;
    }

    public void setScaling(Vector2f scaling) {
        this.scaling.set(scaling);
    }

    public void setPosition(Vector3f pos) {
        this.position.set(pos);
    }

    public RenderSphere calcRenderSphere() {
        float radius = (float) Math.sqrt(this.getScaling().x * this.getScaling().x + this.getScaling().y * this.getScaling().y);
        return new RenderSphere(radius, this.getPosition());
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
        return this.gameRenderTimer.getAccumulatedTime();
    }

    public boolean isDead() {
        return this.dead;
    }
}
