package javagems3d.graphics.particles.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.particles.attributes.ParticleAttributes;
import javagems3d.graphics.particles.objects.base.TexturedParticleFX;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.texturing.packs.ParticleTexturesPack;

public class SimpleTexturedParticle extends TexturedParticleFX {
    private double maxLivingSeconds;

    public SimpleTexturedParticle(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @NotNull ParticleTexturesPack particleTexturesPack, Vector3f pos, Vector2f scaling) {
        super(world, particleAttributes, particleTexturesPack, pos, scaling);
        this.maxLivingSeconds = 1.5;
    }

    @Override
    protected void updateParticle(double frameDeltaTime, IWorld world) {
        this.setPosition(this.getPosition().add(0.0f, (float) (frameDeltaTime), 0.0f));
    }

    @Override
    public double getMaxLivingSeconds() {
        return this.maxLivingSeconds;
    }

    public SimpleTexturedParticle setMaxLivingSeconds(double maxLivingSeconds) {
        this.maxLivingSeconds = maxLivingSeconds;
        return this;
    }

    @Override
    public void setDead() {
    }
}