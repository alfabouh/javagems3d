package ru.alfabouh.jgems3d.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.physics.entities.PhysDynamicEntity;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.World;

public abstract class PhysEntityProp extends PhysDynamicEntity {
    public PhysEntityProp(World world, String name, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, name, properties, scale, pos, rot);
    }

    public PhysEntityProp(World world, String name, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, name, properties, pos, rot);
    }

    public PhysEntityProp(World world, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, scale, pos, rot);
    }

    public PhysEntityProp(World world, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, pos, rot);
    }
}
