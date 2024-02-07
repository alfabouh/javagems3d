package ru.BouH.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.particles.ParticleFlame;
import ru.BouH.engine.physics.particles.SimpleParticle;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;

public class PhysLightCube extends PhysEntityCube {

    public PhysLightCube(World world, RigidBodyObject.PhysProperties properties, Vector3d size, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, size, scale, pos, rot);
    }

    public PhysLightCube(World world, RigidBodyObject.PhysProperties properties, Vector3d size, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, size, pos, rot);
    }
}
