package ru.BouH.engine.physics.triggers.zones;

import ru.BouH.engine.physics.entities.BodyGroup;
import ru.BouH.engine.physics.triggers.ITrigger;
import ru.BouH.engine.physics.triggers.Zone;

public class PickUpItemTriggerZone extends SimpleTriggerZone {
    public PickUpItemTriggerZone(Zone zone, ITrigger onPick) {
        super(zone, onPick, null);
        this.setFilter(BodyGroup.PLAYER.getGroup());
    }
}
