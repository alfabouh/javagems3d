/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.physics.world.triggers.zones;

import ru.jgems3d.engine.physics.world.triggers.ITriggerAction;
import ru.jgems3d.engine.physics.world.triggers.Zone;
import ru.jgems3d.engine.physics.world.triggers.zones.base.AbstractTriggerZone;
import ru.jgems3d.engine.physics.world.IWorld;

public class SimpleTriggerZone extends AbstractTriggerZone {
    private ITriggerAction triggerAction;

    public SimpleTriggerZone(Zone zone) {
        super(zone);
        this.triggerAction = null;
    }

    public void setTriggerAction(ITriggerAction triggerAction) {
        this.triggerAction = triggerAction;
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

    @Override
    public boolean isDead() {
        return false;
    }
}
