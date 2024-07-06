package ru.alfabouh.jgems3d.engine.physics.objects.entities.enemies;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.enemies.ai.AI;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityWithAI extends WorldItem implements IWorldDynamic {
    private final List<AI> aiList;

    public EntityWithAI(World world, Vector3f pos, String itemName) {
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