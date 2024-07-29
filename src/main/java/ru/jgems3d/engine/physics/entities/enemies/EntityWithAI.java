package ru.jgems3d.engine.physics.entities.enemies;

import org.joml.Vector3f;
import ru.jgems3d.engine.physics.entities.enemies.ai.AI;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.jgems3d.engine.physics.world.basic.WorldItem;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityWithAI extends WorldItem implements IWorldTicked {
    private final List<AI> aiList;

    public EntityWithAI(PhysicsWorld world, Vector3f pos, String itemName) {
        super(world, pos, itemName);
        this.aiList = new ArrayList<>();
    }

    public void addAI(AI ai) {
        this.getAiList().add(ai);
    }

    protected List<AI> getAiList() {
        return this.aiList;
    }

    public void onUpdate(IWorld iWorld) {
        this.getAiList().stream().filter(AI::isActive).forEach(e -> e.onUpdate(iWorld));
    }
}
