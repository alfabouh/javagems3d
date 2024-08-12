package ru.jgems3d.engine.physics.world.basic;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.jgems3d.engine.physics.world.ai.EntityAI;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class AIBasedWorldItem extends WorldItem implements IWorldTicked {
    private final List<EntityAI> entityAIList;

    public AIBasedWorldItem(PhysicsWorld world, @NotNull Vector3f pos, @NotNull Vector3f rot, @NotNull Vector3f scaling, String itemName) {
        super(world, pos, rot, scaling, itemName);
        this.entityAIList = new ArrayList<>();
    }

    public AIBasedWorldItem(PhysicsWorld world, Vector3f pos, Vector3f rot, String itemName) {
        this(world, pos, rot, new Vector3f(1.0f), itemName);
    }

    public AIBasedWorldItem(PhysicsWorld world, Vector3f pos, String itemName) {
        this(world, pos, new Vector3f(0.0f), new Vector3f(1.0f), itemName);
    }

    public abstract void init(AIBasedWorldItem aiBasedWorldItem);

    @Override
    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.getEntityAIList().forEach(e -> e.onStartAI(this));
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.getEntityAIList().forEach(e -> e.onEndAI(this));
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        for (EntityAI entityAI : this.getEntityAIList()) {
            if (entityAI.getState() == EntityAI.State.ENABLED) {
                entityAI.onUpdateAI(this);
            }
        }
    }

    public void addNewAI(EntityAI ai) {
        this.getEntityAIList().add(ai);
        this.getEntityAIList().sort(Comparator.comparingInt(EntityAI::priority).thenComparingInt(System::identityHashCode));
    }

    public List<EntityAI> getEntityAIList() {
        return this.entityAIList;
    }
}
