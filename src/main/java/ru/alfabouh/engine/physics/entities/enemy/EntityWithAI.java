package ru.alfabouh.engine.physics.entities.enemy;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.engine.physics.entities.enemy.ai.AI;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.object.IWorldDynamic;
import ru.alfabouh.engine.physics.world.object.WorldItem;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityWithAI extends WorldItem implements IWorldDynamic {
    private final List<AI> aiList;

    public EntityWithAI(World world, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        super(world, scale, pos, rot, itemName);
        this.aiList = new ArrayList<>();
    }

    public EntityWithAI(World world, double scale, Vector3d pos, String itemName) {
        this(world, scale, pos, new Vector3d(0.0d), itemName);
    }

    public EntityWithAI(World world, double scale, String itemName) {
        this(world, scale, new Vector3d(0.0d), new Vector3d(0.0d), itemName);
    }

    public EntityWithAI(World world, @NotNull Vector3d pos, @NotNull Vector3d rot, String itemName) {
        this(world, 1.0d, pos, rot, itemName);
    }

    public EntityWithAI(World world, Vector3d pos, String itemName) {
        this(world, 1.0d, pos, new Vector3d(0.0d), itemName);
    }

    public EntityWithAI(World world, String itemName) {
        this(world, 1.0d, new Vector3d(0.0d), new Vector3d(0.0d), itemName);
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
