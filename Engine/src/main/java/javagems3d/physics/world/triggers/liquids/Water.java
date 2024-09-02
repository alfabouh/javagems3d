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

package javagems3d.physics.world.triggers.liquids;

import javagems3d.physics.entities.properties.state.EntityState;
import javagems3d.physics.entities.properties.state.IHasEntityState;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.base.Liquid;

public class Water extends Liquid {
    public Water(Zone zone) {
        super(zone);
    }

    @Override
    protected void onEntityEnteredLiquid(Object e) {
        if (e instanceof IHasEntityState) {
            IHasEntityState entityState = (IHasEntityState) e;
            entityState.getEntityState().setState(EntityState.Type.IN_LIQUID);
        }
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
