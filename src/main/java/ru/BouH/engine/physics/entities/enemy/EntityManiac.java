package ru.BouH.engine.physics.entities.enemy;

import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.physics.entities.enemy.ai.NavigationAI;
import ru.BouH.engine.physics.entities.enemy.ai.NavigationToPlayerAI;
import ru.BouH.engine.physics.world.World;

public class EntityManiac extends EntityWithAI {
    private final NavigationToPlayerAI navigationAI;

    public EntityManiac(World world, Vector3d pos) {
        super(world, pos, "maniac");
        this.navigationAI = new NavigationToPlayerAI(0.1d, this, world.getGraph());
        this.navigationAI.setPlayer(Game.getGame().getPlayerSP());
        this.addAI(this.navigationAI);
    }

    public NavigationAI getNavigationAI() {
        return this.navigationAI;
    }
}
