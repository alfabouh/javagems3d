package ru.jgems3d.engine.physics.world.triggers.liquids;

import ru.jgems3d.engine.physics.entities.properties.state.EntityState;
import ru.jgems3d.engine.physics.entities.properties.state.IHasEntityState;
import ru.jgems3d.engine.physics.world.triggers.Zone;
import ru.jgems3d.engine.physics.world.triggers.liquids.base.Liquid;

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
