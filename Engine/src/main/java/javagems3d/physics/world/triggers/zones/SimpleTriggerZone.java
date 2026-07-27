/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.physics.world.triggers.zones;

import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.Zone;

public class SimpleTriggerZone extends AbstractTriggerZone {
    private ITriggerAction triggerAction;

    public SimpleTriggerZone(Zone zone) {
        super(zone);
        this.triggerAction = null;
    }

    @Override
    public ITriggerAction collisionTriggerFunc() {
        return this.getTriggerFunc();
    }

    @Override
    public void onUpdate(IWorld iWorld) {

    }

    public ITriggerAction getTriggerFunc() {
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
