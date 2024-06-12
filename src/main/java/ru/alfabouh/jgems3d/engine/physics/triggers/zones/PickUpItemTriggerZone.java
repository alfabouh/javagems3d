package ru.alfabouh.jgems3d.engine.physics.triggers.zones;

import ru.alfabouh.jgems3d.engine.physics.entities.BodyGroup;
import ru.alfabouh.jgems3d.engine.physics.triggers.ITrigger;
import ru.alfabouh.jgems3d.engine.physics.triggers.Zone;

public class PickUpItemTriggerZone extends SimpleTriggerZone {
    public PickUpItemTriggerZone(Zone zone, ITrigger onPick) {
        super(zone, onPick, null);
        this.setFilter(BodyGroup.PLAYER.getGroup());
    }
}
