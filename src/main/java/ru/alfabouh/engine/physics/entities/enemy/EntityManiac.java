package ru.alfabouh.engine.physics.entities.enemy;

import org.joml.Vector3d;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.physics.entities.enemy.ai.NavigationAI;
import ru.alfabouh.engine.physics.entities.enemy.ai.NavigationToPlayerAI;
import ru.alfabouh.engine.physics.world.World;

public class EntityManiac extends EntityWithAI {
    private final NavigationToPlayerAI navigationAI;

    public EntityManiac(World world, Vector3d pos) {
        super(world, pos, "maniac");
        this.navigationAI = new NavigationToPlayerAI(0.01d, this, world);
        this.navigationAI.setPlayer(Game.getGame().getPlayerSP());
        this.addAI(this.navigationAI);
    }

    public boolean canBeDestroyed() {
        return false;
    }

    public NavigationAI getNavigationAI() {
        return this.navigationAI;
    }
}
