package ru.BouH.engine.physics.entities.prop;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.entities.PhysDynamicEntity;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;

public abstract class PhysEntityProp extends PhysDynamicEntity {
    private final Vector3d size;

    public PhysEntityProp(World world, RigidBodyObject.PhysProperties properties, Vector3d size, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, properties, scale, pos, rot);
        this.size = new Vector3d(size);
    }

    public PhysEntityProp(World world, RigidBodyObject.PhysProperties properties, Vector3d size, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, properties, size, 1.0d, pos, rot);
    }

    public Vector3d getSize() {
        return this.size;
    }
}
