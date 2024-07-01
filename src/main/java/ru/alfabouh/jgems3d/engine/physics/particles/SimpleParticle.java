package ru.alfabouh.jgems3d.engine.physics.particles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.world.World;

public class SimpleParticle extends ParticleFX {

    public SimpleParticle(World world, Vector3f scale, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, scale, pos, rot);
    }
}
