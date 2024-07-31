package ru.jgems3d.engine.graphics.opengl.particles.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.particles.attributes.ParticleAttributes;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ParticleTexturePack;

public class SimpleParticle extends ParticleFX {
    private double maxLivingSeconds;

    public SimpleParticle(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @NotNull ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
        super(world, particleAttributes, particleTexturePack, pos, scaling);
        this.maxLivingSeconds = 1.5;
    }

    @Override
    protected void updateParticle(double partialTicks, IWorld world) {
        this.setPosition(this.getPosition().add(0.0f, (float) (0.01f * partialTicks), 0.0f));
    }

    public SimpleParticle setMaxLivingSeconds(double maxLivingSeconds) {
        this.maxLivingSeconds = maxLivingSeconds;
        return this;
    }

    @Override
    public double getMaxLivingSeconds() {
        return this.maxLivingSeconds;
    }
}