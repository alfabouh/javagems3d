package ru.BouH.engine.physics.brush;

import org.joml.Vector3d;
import ru.BouH.engine.physics.collision.AbstractCollision;
import ru.BouH.engine.physics.collision.ConvexShape;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;

public class Plane4dBrush extends WorldBrush {
    private final Vector3d[] vertices;

    public Plane4dBrush(World world, RigidBodyObject.PhysProperties properties, Vector3d[] vertices, String name) {
        super(world, properties, name);
        this.vertices = vertices;
    }

    public Plane4dBrush(World world, RigidBodyObject.PhysProperties properties, Vector3d[] vertices) {
        this(world, properties, vertices, "brush_plane");
    }

    public Vector3d[] getVertices() {
        return this.vertices;
    }

    @Override
    protected AbstractCollision constructCollision() {
        return new ConvexShape(this.getVertices());
    }
}
