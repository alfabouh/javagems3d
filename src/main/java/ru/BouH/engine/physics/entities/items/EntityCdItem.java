package ru.BouH.engine.physics.entities.items;

import org.joml.Vector3d;
import ru.BouH.engine.audio.sound.data.SoundType;
import ru.BouH.engine.game.Game;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.physics.entities.player.KinematicPlayerSP;
import ru.BouH.engine.physics.triggers.Zone;
import ru.BouH.engine.physics.triggers.zones.PickUpItemTriggerZone;
import ru.BouH.engine.physics.world.IWorld;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.physics.world.object.WorldItem;

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
