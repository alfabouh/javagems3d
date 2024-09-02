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

package javagems3d.graphics.opengl.particles.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.graphics.opengl.particles.attributes.ParticleAttributes;
import javagems3d.graphics.opengl.particles.objects.base.ColoredParticleFX;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.world.IWorld;

public class SimpleColoredParticle extends ColoredParticleFX {
    private double maxLivingSeconds;

    public SimpleColoredParticle(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @NotNull Vector3f color, Vector3f pos, Vector2f scaling) {
        super(world, particleAttributes, color, pos, scaling);
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

    public SimpleColoredParticle setMaxLivingSeconds(double maxLivingSeconds) {
        this.maxLivingSeconds = maxLivingSeconds;
        return this;
    }
}