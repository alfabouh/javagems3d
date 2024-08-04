package ru.jgems3d.engine.graphics.opengl.particles.objects.base;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.particles.attributes.ParticleAttributes;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;

public abstract class ColoredParticleFX extends ParticleFX {
    public ColoredParticleFX(SceneWorld world, @NotNull ParticleAttributes particleAttributes, @NotNull Vector3f color, Vector3f pos, Vector2f scaling) {
        super(world, particleAttributes, null, pos, scaling);
        this.setColorMask(color);
    }
}
