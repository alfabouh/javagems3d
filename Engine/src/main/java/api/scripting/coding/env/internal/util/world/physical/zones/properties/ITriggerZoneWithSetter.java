package api.scripting.coding.env.internal.util.world.physical.zones.properties;

import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.zones.base.ITriggerZone;

public interface ITriggerZoneWithSetter extends ITriggerZone {
    void setTriggerAction(ITriggerAction action);
}