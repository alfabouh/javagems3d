package ru.alfabouh.jgems3d.engine.system.proxy;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.IPlayer;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.KinematicPlayerSP;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.timer.PhysicsTimer;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;

public class LocalPlayer {
    private final IPlayer entityPlayerSP;

    public LocalPlayer(World world) {
        this.entityPlayerSP = new KinematicPlayerSP(world, new Vector3f(0.0f), new Vector3f(0.0f));
    }

    public IPlayer getEntityPlayerSP() {
        return this.entityPlayerSP;
    }

    public void addPlayerInWorlds(Vector3f position, double rotation) {
        KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) this.getEntityPlayerSP();
        kinematicPlayerSP.createPlayer();
        KinematicPlayerSP physPlayerSP = (KinematicPlayerSP) this.entityPlayerSP;
        synchronized (PhysicsTimer.lockObject) {
            kinematicPlayerSP.getWorld().addInBulletWorld(physPlayerSP.getBulletObject(), kinematicPlayerSP.getBodyIndex());
            kinematicPlayerSP.getWorld().getDynamicsWorld().addAction(kinematicPlayerSP.getKinematicCharacterController());
        }
        kinematicPlayerSP.setCanPlayerJump(false);

        kinematicPlayerSP.getWorld().addItem(kinematicPlayerSP);
        JGems.get().getScreen().getRenderWorld().addItem(kinematicPlayerSP, ResourceManager.renderDataAssets.player);
        physPlayerSP.setCollisionTranslation(position);
        physPlayerSP.setRotation(new Vector3f(0.0f, (float) -Math.toDegrees(rotation), 0.0f));
    }
}