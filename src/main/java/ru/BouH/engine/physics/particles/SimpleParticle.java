package ru.BouH.engine.physics.particles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.world.World;

public class SimpleParticle extends ParticleFX {
    public SimpleParticle(World world, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, scale, pos, rot);
    }
}
