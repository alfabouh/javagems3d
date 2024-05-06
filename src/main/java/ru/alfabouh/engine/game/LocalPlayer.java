package ru.alfabouh.engine.game;

import org.joml.Vector3d;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.physics.entities.player.IPlayer;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.timer.PhysicsTimer;

public class LocalPlayer {
    private final IPlayer entityPlayerSP;

    public LocalPlayer(World world) {
        this.entityPlayerSP = new KinematicPlayerSP(world, new Vector3d(0.0d), new Vector3d(0.0d));
    }

    public IPlayer getEntityPlayerSP() {
        return this.entityPlayerSP;
    }

    public void addPlayerInWorlds(Vector3d position) {
        KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) this.getEntityPlayerSP();
        kinematicPlayerSP.createPlayer();
        KinematicPlayerSP physPlayerSP = (KinematicPlayerSP) this.entityPlayerSP;
        synchronized (PhysicsTimer.lock) {
            kinematicPlayerSP.getWorld().addInBulletWorld(physPlayerSP.getBulletObject(), kinematicPlayerSP.getBodyIndex());
            kinematicPlayerSP.getWorld().getDynamicsWorld().addAction(kinematicPlayerSP.getKinematicCharacterController());
        }
        kinematicPlayerSP.setCanPlayerJump(false);

        kinematicPlayerSP.getWorld().addItem(kinematicPlayerSP);
        Game.getGame().getScreen().getRenderWorld().addItem(kinematicPlayerSP, ResourceManager.renderDataAssets.player);
        physPlayerSP.setCollisionTranslation(position);
        physPlayerSP.setRotation(new Vector3d(0.0f, -90.0f, 0.0f));
    }
}