package ru.alfabouh.engine.physics.triggers.zones;

import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.triggers.ITrigger;
import ru.alfabouh.engine.physics.triggers.Zone;

public class PickUpItemTriggerZone extends SimpleTriggerZone {
    public PickUpItemTriggerZone(Zone zone, ITrigger onPick) {
        super(zone, onPick, null);
        this.setFilter(BodyGroup.PLAYER.getGroup());
    }
}
