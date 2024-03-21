package ru.BouH.engine.game;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletDynamics.btDynamicsWorld;
import org.joml.Vector3d;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.entities.player.IPlayer;
import ru.BouH.engine.physics.entities.player.KinematicPlayerSP;
import ru.BouH.engine.physics.world.World;

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
        kinematicPlayerSP.getWorld().getDynamicsWorld().addCollisionObject(physPlayerSP.getBulletObject(), btBroadphaseProxy.CharacterFilter, btBroadphaseProxy.AllFilter);
        kinematicPlayerSP.getWorld().getDynamicsWorld().addAction(kinematicPlayerSP.getKinematicCharacterController());
        Game.getGame().getProxy().addItemInWorlds(physPlayerSP, ResourceManager.renderDataAssets.player);
        physPlayerSP.setPosition(position);
    }
}
