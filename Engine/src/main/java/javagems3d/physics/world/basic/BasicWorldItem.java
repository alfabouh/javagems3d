package javagems3d.physics.world.basic;

import javagems3d.physics.world.PhysicsWorld;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BasicWorldItem extends WorldItem {
    public BasicWorldItem(PhysicsWorld world, @NotNull Vector3f position, @NotNull Vector3f rotation, @NotNull Vector3f scaling, String itemName) {
        super(world, position, rotation, scaling, itemName);
    }

    public BasicWorldItem(PhysicsWorld world, Vector3f position, Vector3f rotation, String itemName) {
        super(world, position, rotation, itemName);
    }

    public BasicWorldItem(PhysicsWorld world, Vector3f position, String itemName) {
        super(world, position, itemName);
    }

    public BasicWorldItem(PhysicsWorld world, String itemName) {
        super(world, itemName);
    }
}
