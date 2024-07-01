package ru.alfabouh.jgems3d.engine.physics.objects.entities.items;

import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.audio.sound.data.SoundType;
import ru.alfabouh.jgems3d.engine.physics.objects.entities.player.KinematicPlayerSP;
import ru.alfabouh.jgems3d.engine.physics.triggers.Zone;
import ru.alfabouh.jgems3d.engine.physics.triggers.zones.PickUpItemTriggerZone;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;
import ru.alfabouh.jgems3d.engine.physics.world.object.WorldItem;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

public class EntityCassetteItem extends WorldItem {
    private PickUpItemTriggerZone pickUpItemTriggerZone;

    public EntityCassetteItem(World world, Vector3f pos, String itemName) {
        super(world, pos, itemName);
    }

    public void onSpawn(IWorld iWorld) {
        super.onSpawn(iWorld);
        this.pickUpItemTriggerZone = new PickUpItemTriggerZone(new Zone(this.getPosition(), new Vector3f(1.0f)),
                                                               (e) -> {
                                                                   if (e instanceof KinematicPlayerSP) {
                                                                       KinematicPlayerSP kinematicPlayerSP = (KinematicPlayerSP) e;
                                                                       kinematicPlayerSP.setPrickedCassettes(kinematicPlayerSP.getPrickedCassettes() + 1);
                                                                       JGems.get().getSoundManager().playLocalSound(ResourceManager.soundAssetsLoader.pick, SoundType.BACKGROUND_SOUND, 1.0f, 1.0f);
                                                                       SystemLogging.get().getLogManager().log("Put cassette in inventory!");
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
