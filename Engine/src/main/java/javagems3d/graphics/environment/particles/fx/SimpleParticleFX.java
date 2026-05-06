package javagems3d.graphics.environment.particles.fx;

import javagems3d.graphics.environment.particles.data.ParticleFXRenderData;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.IWorld;
import org.jetbrains.annotations.NotNull;

public class SimpleParticleFX extends ParticleFX {
    public SimpleParticleFX(@NotNull ParticleFXRenderData particleFXRenderData) {
        super(particleFXRenderData);
    }

    @Override
    public float interpolationPoint() {
        return 0;
    }


    @Override
    public void onSpawn(IWorld iWorld) {

    }

    @Override
    public void onDestroy(IWorld iWorld) {

    }

    @Override
    public void onUpdate(IWorld iWorld) {

    }
}
