package ru.BouH.engine.physics.triggers;

import ru.BouH.engine.physics.entities.BodyGroup;

public class DoorTriggerZone extends SimpleTriggerZone {
    public DoorTriggerZone(Zone zone) {
        super(zone, null, null);
        this.setFilter(BodyGroup.PLAYER.getGroup());
    }

    public boolean isActive() {
        return this.btEnteredBodies.isEmpty();
    }
}
