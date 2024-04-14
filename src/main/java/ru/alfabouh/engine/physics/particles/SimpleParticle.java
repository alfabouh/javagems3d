package ru.alfabouh.engine.physics.particles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.engine.physics.world.World;

public class SimpleParticle extends ParticleFX {
    public SimpleParticle(World world, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, scale, pos, rot);
    }
}
