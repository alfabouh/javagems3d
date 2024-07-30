package ru.jgems3d.engine.physics.entities.enemies;

import org.joml.Vector3f;
import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.physics.entities.enemies.ai.NavigationAI;
import ru.jgems3d.engine.physics.entities.enemies.ai.NavigationToPlayerAI;
import ru.jgems3d.engine.physics.world.PhysicsWorld;

public class EntityManiac extends EntityWithAI {
    private final NavigationToPlayerAI navigationAI;

    public EntityManiac(PhysicsWorld world, Vector3f pos) {
        super(world, pos, "maniac");
        this.navigationAI = new NavigationToPlayerAI(0.01d, this, world);
        this.navigationAI.setPlayer(JGems3D.get().getPlayer());
        this.addAI(this.navigationAI);
    }

    public boolean canBeDestroyed() {
        return false;
    }

    public NavigationAI getNavigationAI() {
        return this.navigationAI;
    }
}
