package ru.alfabouh.engine.physics.entities.items;

import org.joml.Vector3d;
import ru.alfabouh.engine.audio.sound.data.SoundType;
import ru.alfabouh.engine.game.Game;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.physics.entities.player.KinematicPlayerSP;
import ru.alfabouh.engine.physics.triggers.Zone;
import ru.alfabouh.engine.physics.triggers.zones.PickUpItemTriggerZone;
import ru.alfabouh.engine.physics.world.IWorld;
import ru.alfabouh.engine.physics.world.World;
import ru.alfabouh.engine.physics.world.object.WorldItem;

public class EntityCdItem extends WorldItem {
    private PickUpItemTriggerZone pickUpItemTriggerZone;

    public EntityCdItem(World world, Vector3d pos, String itemName) {
        super(world, pos, itemName);
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.pickUpItemTriggerZone = new PickUpItemTriggerZone(new Zone(this.getPosition(), new Vector3d(1.0d)),
                                                               (e) -> {
                                                                   if (e instanceof KinematicPlayerSP) {
                                                                       KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) e;
                                                                       kinematicPlayerSP.setPickedCds(kinematicPlayerSP.getPickedCds() + 1);
                                                                       Game.getGame().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.pick, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
                                                                       Game.getGame().getLogManager().log("Put cd in inventory!");
                                                                       this.setDead();
                                                                   }
                                                               });
        this.getWorld().addTriggerZone(this.getPickUpItemTriggerZone());
    }

    public void onDestroy(IWorld iWorld) {
        super.onDestroy(iWorld);
        this.getWorld().removeTriggerZone(this.getPickUpItemTriggerZone());
    }

    public PickUpItemTriggerZone getPickUpItemTriggerZone() {
        return this.pickUpItemTriggerZone;
    }
}
