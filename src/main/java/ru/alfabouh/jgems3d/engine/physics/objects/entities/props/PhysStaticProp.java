package ru.alfabouh.jgems3d.engine.physics.objects.entities.props;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.jgems3d.engine.physics.objects.base.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;

public class PhysStaticProp extends PhysProp {
    public PhysStaticProp(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, Vector3f rot, Vector3f scale, MeshDataGroup meshDataGroup) {
        super(world, name, properties, pos, rot, scale, meshDataGroup);
    }

    public PhysStaticProp(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, Vector3f rot, MeshDataGroup meshDataGroup) {
        super(world, name, properties, pos, rot, meshDataGroup);
    }

    public PhysStaticProp(World world, String name, RigidBodyObject.PhysProperties properties, Vector3f pos, MeshDataGroup meshDataGroup) {
        super(world, name, properties, pos, meshDataGroup);
    }

    @Override
    public BodyGroup getBodyIndex() {
        return BodyGroup.ENTITY_STATIC;
    }
}
