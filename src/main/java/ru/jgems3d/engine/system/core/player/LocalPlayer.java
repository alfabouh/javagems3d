package ru.jgems3d.engine.system.core.player;

import org.joml.Vector3f;
import ru.jgems3d.engine.physics.entities.player.Player;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class LocalPlayer {
    private final IPlayerConstructor playerConstructor;
    private Player player;

    public LocalPlayer(IPlayerConstructor playerConstructor) {
        this.playerConstructor = playerConstructor;
    }

    public void addPlayerInWorlds(PhysicsWorld world, Vector3f startPos, Vector3f startRot) {
        Player dynamicPlayer = this.playerConstructor.constructPlayer(world, new Vector3f(startPos), new Vector3f(startRot));
        JGemsHelper.WORLD.addItemInWorld(dynamicPlayer, JGemsResourceManager.globalRenderDataAssets.player);
        this.player = dynamicPlayer;
    }

    public Player getEntityPlayer() {
        return this.player;
    }
}