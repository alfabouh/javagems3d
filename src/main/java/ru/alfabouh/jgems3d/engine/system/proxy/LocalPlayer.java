package ru.alfabouh.jgems3d.engine.system.proxy;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.entities.player.IPlayer;
import ru.alfabouh.jgems3d.engine.physics.entities.player.KinematicPlayer;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.system.resources.manager.JGemsResourceManager;

public class LocalPlayer {
    private final IPlayer entityPlayerSP;

    public LocalPlayer(World world, Vector3f position, Vector3f rotation) {
        this.entityPlayerSP = new KinematicPlayer(world, new Vector3f(position), new Vector3f(rotation));
    }

    public void addPlayerInWorlds() {
        KinematicPlayer dynamicPlayer = (KinematicPlayer) this.getEntityPlayerSP();
        dynamicPlayer.getWorld().addItem(dynamicPlayer);
        JGems.get().getScreen().getRenderWorld().addItem(dynamicPlayer, JGemsResourceManager.renderDataAssets.player);
    }

    public IPlayer getEntityPlayerSP() {
        return this.entityPlayerSP;
    }
}