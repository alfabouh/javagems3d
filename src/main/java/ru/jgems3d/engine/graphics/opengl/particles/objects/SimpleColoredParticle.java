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

package ru.jgems3d.engine.graphics.opengl.particles.objects;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.particles.attributes.ParticleAttributes;
import ru.jgems3d.engine.graphics.opengl.particles.objects.base.ColoredParticleFX;
import ru.jgems3d.engine.graphics.opengl.particles.objects.base.TexturedParticleFX;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.system.resources.assets.material.samples.packs.ParticleTexturePack;

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

    public SimpleColoredParticle setMaxLivingSeconds(double maxLivingSeconds) {
        this.maxLivingSeconds = maxLivingSeconds;
        return this;
    }

    @Override
    public double getMaxLivingSeconds() {
        return this.maxLivingSeconds;
    }
}