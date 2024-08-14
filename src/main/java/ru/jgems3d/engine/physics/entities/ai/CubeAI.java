package ru.jgems3d.engine.physics.entities.ai;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.ai.navigation.MTNavigationAI;
import ru.jgems3d.engine.physics.world.ai.navigation.NavigationAI;
import ru.jgems3d.engine.physics.world.basic.AIBasedWorldItem;

public class CubeAI extends AIBasedWorldItem {
    private NavigationAI<CubeAI> ai;

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
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        if (!this.ai.hasPath()) {
            System.out.println("FF");
            this.ai.setDestination(JGems3D.get().getPlayer());
        }
    }

    @Override
    public void init(AIBasedWorldItem aiBasedWorldItem) {
        this.ai = new MTNavigationAI<>(this, 0);
        this.addNewAI(this.ai);
    }
}
