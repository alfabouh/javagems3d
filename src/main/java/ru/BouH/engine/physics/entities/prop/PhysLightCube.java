package ru.BouH.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;

public class PhysLightCube extends PhysCube {
    public PhysLightCube(World world, RigidBodyObject.PhysProperties properties, Vector3d size, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, size, scale, pos, rot);
    }
}
