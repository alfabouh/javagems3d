package ru.alfabouh.jgems3d.engine.physics.particles;

import org.bytedeco.bullet.BulletCollision.btCollisionObject;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.collision.base.AbstractCollision;
import ru.alfabouh.jgems3d.engine.physics.collision.primitive.OBBShape;
import ru.alfabouh.jgems3d.engine.physics.objects.base.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.objects.base.PhysEntity;
import ru.alfabouh.jgems3d.engine.physics.objects.materials.Materials;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;

public abstract class ParticleFX extends PhysEntity {
    protected int lifeTime;

    public ParticleFX(World world, Vector3f scale, @NotNull Vector3f pos, @NotNull Vector3f rot) {
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
        return new OBBShape(new Vector3f(0.05f, 0.05f, 0.05f));
    }

    protected void afterRigidBodyCreated(RigidBodyObject rigidBodyObject) {
        rigidBodyObject.setCollisionFlags(btCollisionObject.CF_NO_CONTACT_RESPONSE);
    }

    @Override
    public BodyGroup getBodyIndex() {
        return BodyGroup.PARTICLE;
    }
}
