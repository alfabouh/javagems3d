package javagems3d.physics.world.triggers.zones;

import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.zones.base.AbstractTriggerZone;

public class SimpleTriggerZone extends AbstractTriggerZone {
    private ITriggerAction triggerAction;

    public SimpleTriggerZone(Zone zone) {
        super(zone);
        this.triggerAction = null;
    }

    @Override
    public ITriggerAction onColliding() {
        return this.getTriggerAction();
    }

    @Override
    public void onUpdate(IWorld iWorld) {

    }

    public ITriggerAction getTriggerAction() {
        return this.triggerAction;
    }

    public void setTriggerAction(ITriggerAction triggerAction) {
        this.triggerAction = triggerAction;
    }

    @Override
    public void setDead() {
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
