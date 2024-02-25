package ru.BouH.engine.proxy;

import org.bytedeco.bullet.BulletCollision.btBroadphaseProxy;
import org.bytedeco.bullet.BulletDynamics.btDynamicsWorld;
import org.joml.Vector3d;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.entities.Materials;
import ru.BouH.engine.physics.entities.player.DynamicPlayerSP;
import ru.BouH.engine.physics.entities.player.IPlayer;
import ru.BouH.engine.physics.entities.player.KinematicPlayerSP;
import ru.BouH.engine.physics.jb_objects.RigidBodyObject;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;

public class LocalPlayer {
    public static boolean VALID_PL = false;
    private final IPlayer entityPlayerSP;

    public LocalPlayer(World world, Vector3d pos) {
        this.entityPlayerSP = new KinematicPlayerSP(world, pos, new Vector3d(0.0d));
        //this.entityPlayerSP = new DynamicPlayerSP(world, RigidBodyObject.PhysProperties.createProperties(Materials.defaultMaterial, false, 10.0d), pos, new Vector3d(0.0d));
    }

    public IPlayer getEntityPlayerSP() {
        return this.entityPlayerSP;
    }

    public void addPlayerInWorlds(btDynamicsWorld dynamicsWorld) {
        KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) this.getEntityPlayerSP();
        kinematicPlayerSP.createPlayer();
        KinematicPlayerSP physPlayerSP = (KinematicPlayerSP) this.entityPlayerSP;
        dynamicsWorld.addCollisionObject(physPlayerSP.getBulletObject(), btBroadphaseProxy.CharacterFilter, btBroadphaseProxy.AllFilter);
        dynamicsWorld.addAction(kinematicPlayerSP.getKinematicCharacterController());
        Game.getGame().getProxy().addItemInWorlds(physPlayerSP, ResourceManager.renderDataAssets.player);
        LocalPlayer.VALID_PL = true;
    }
}
