package ru.alfabouh.jgems3d.engine.physics.entities.enemies;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.entities.enemies.ai.NavigationAI;
import ru.alfabouh.jgems3d.engine.physics.entities.enemies.ai.NavigationToPlayerAI;
import ru.alfabouh.jgems3d.engine.physics.world.World;

public class EntityManiac extends EntityWithAI {
    private final NavigationToPlayerAI navigationAI;

    public EntityManiac(World world, Vector3f pos) {
        super(world, pos, "maniac");
        this.navigationAI = new NavigationToPlayerAI(0.01d, this, world);
        this.navigationAI.setPlayer(JGems.get().getPlayerSP());
        this.addAI(this.navigationAI);
    }

    public boolean canBeDestroyed() {
        return false;
    }

    public NavigationAI getNavigationAI() {
        return this.navigationAI;
    }
}
