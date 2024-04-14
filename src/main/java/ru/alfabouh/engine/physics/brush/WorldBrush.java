package ru.alfabouh.engine.physics.brush;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.entities.PhysEntity;
import ru.alfabouh.engine.physics.jb_objects.RigidBodyObject;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;

public abstract class WorldBrush extends PhysEntity {

    public WorldBrush(World world, RigidBodyObject.PhysProperties properties, String name) {
        super(world, name, properties, 1.0d, new Vector3d(0.0d), new Vector3d(0.0d));
    }

    public WorldBrush(World world, RigidBodyObject.PhysProperties properties, String name, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, name, properties, pos, rot);
    }

    public WorldBrush(World world, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, "brush_ent", properties, pos, rot);
    }

    public WorldBrush(World world, RigidBodyObject.PhysProperties properties, @NotNull Vector3d pos) {
        super(world, "brush_ent", properties, pos, new Vector3d(0.0d));
    }

    public WorldBrush(World world, RigidBodyObject.PhysProperties properties) {
        this(world, properties, "brush_ent");
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
    }

    @Override
    public BodyGroup getBodyIndex() {
        return BodyGroup.BRUSH;
    }

    public boolean canBeDestroyed() {
        return false;
    }
}
