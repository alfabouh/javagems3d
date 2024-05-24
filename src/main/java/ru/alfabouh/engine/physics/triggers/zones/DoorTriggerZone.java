package ru.alfabouh.engine.physics.triggers.zones;

import ru.alfabouh.engine.physics.entities.BodyGroup;
import ru.alfabouh.engine.physics.triggers.Zone;

public class DoorTriggerZone extends SimpleTriggerZone {
    public DoorTriggerZone(Zone zone) {
        super(zone, null, null);
        this.setFilter(BodyGroup.PLAYER.getGroup());
    }

    public boolean isActive() {
        return this.btEnteredBodies.isEmpty();
    }
}