package javagems3d.physics.world.triggers.liquids;

import javagems3d.physics.entities.properties.state.EntityState;
import javagems3d.physics.entities.properties.state.IHasEntityState;
import javagems3d.physics.world.triggers.Zone;

public class Water extends Liquid {
    public Water(Zone zone) {
        super(zone);
    }

    @Override
    protected void onEntityCollideLiquid(Object e, long pointId) {
        if (e instanceof IHasEntityState entityState) {
            entityState.getEntityState().setState(EntityState.Type.IN_LIQUID);
        }
    }

    @Override
    public void setDead() {
    }

    @Override
    public boolean isDead() {
        return false;
    }
}
