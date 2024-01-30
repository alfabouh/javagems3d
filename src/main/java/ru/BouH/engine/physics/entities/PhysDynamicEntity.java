package ru.BouH.engine.physics.entities;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.IWorldDynamic;
import ru.BouH.engine.proxy.IWorld;

public abstract class PhysDynamicEntity extends PhysEntity implements IWorldDynamic {
    private final Vector3d velocityVector;

    public PhysDynamicEntity(World world, String name, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, name, properties, scale, pos, rot);
        this.velocityVector = new Vector3d(0.0d);
    }

    public PhysDynamicEntity(World world, String name, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, name, properties, 1.0d, pos, rot);
    }

    public PhysDynamicEntity(World world, RigidBodyObject.PhysProperties properties, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, "phys_dynamic_ent", properties, scale, pos, rot);
    }

    public PhysDynamicEntity(World world, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        this(world, "phys_dynamic_ent", properties, 1.0d, pos, rot);
    }

    public Vector3d getVelocityVector() {
        return new Vector3d(this.velocityVector);
    }

    public void setVelocityVector(Vector3d vector3d) {
        this.velocityVector.set(vector3d);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.isValid()) {
            this.addObjectVelocity(this.getVelocityVector());
            this.setVelocityVector(new Vector3d(0.0d));
            if (this.getPosition().y <= -10 || this.getPosition().y >= 500) {
                this.setPosition(new Vector3d(0, 5, 0));
                this.setObjectVelocity(new Vector3d(0.0d));
            }
        }
    }
}
