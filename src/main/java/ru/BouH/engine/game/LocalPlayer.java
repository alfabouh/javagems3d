package ru.BouH.engine.game;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.entities.player.IPlayer;
import ru.BouH.engine.physics.entities.player.KinematicPlayerSP;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.timer.PhysicsTimer;

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
        Game.getGame().getProxy().addItemInWorlds(physPlayerSP, ResourceManager.renderDataAssets.player);
        physPlayerSP.setCollisionTranslation(position);
    }
}