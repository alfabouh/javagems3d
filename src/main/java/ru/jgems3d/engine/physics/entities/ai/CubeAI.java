package ru.jgems3d.engine.physics.entities.ai;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.ai.navigation.NavigationAI;
import ru.jgems3d.engine.physics.world.basic.AIBasedWorldItem;

public class CubeAI extends AIBasedWorldItem {
    public CubeAI(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
    }

    public CubeAI(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        super(world, pos, rot, itemName);
    }

    public CubeAI(PhysicsWorld world, Vector3f pos, String itemName) {
        super(world, pos, itemName);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);

    }

    @Override
    public void init(AIBasedWorldItem aiBasedWorldItem) {
        this.addNewAI(new NavigationAI<>(this, 0));
    }
}
