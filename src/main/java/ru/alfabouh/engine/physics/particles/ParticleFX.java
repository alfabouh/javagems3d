package ru.alfabouh.engine.physics.particles;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.engine.physics.collision.AbstractCollision;
import ru.alfabouh.engine.physics.collision.OBB;
import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.entities.Materials;
import ru.alfabouh.engine.physics.entities.PhysDynamicEntity;
import ru.alfabouh.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;

public abstract class ParticleFX extends PhysDynamicEntity {
    protected int lifeTime;

    public ParticleFX(World world, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, "particle_fx", RigidBodyObject.PhysProperties.createProperties(Materials.particle, false, 0.1f), scale, pos, rot);
        this.lifeTime = this.lifeTime();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        if (this.isValid()) {
            if (this.getTicksExisted() >= this.lifeTime) {
                this.setDead();
            }
        }
    }

    protected int lifeTime() {
        return 200;
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new OBB(new Vector3d(0.05d, 0.05d, 0.05d));
    }

    protected void afterRigidBodyCreated(RigidBodyObject rigidBodyObject) {
        rigidBodyObject.setCollisionFlags(btCollisionObject.CF_NO_CONTACT_RESPONSE);
    }

    @Override
    public BodyGroup getBodyIndex() {
        return BodyGroup.PARTICLE;
    }
}