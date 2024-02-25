package ru.BouH.engine.physics.particles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.BouH.engine.math.MathHelper;
import ru.BouH.engine.physics.world.World;
import ru.BouH.engine.proxy.IWorld;

public class ParticleFlame extends SimpleParticle {
    public ParticleFlame(World world, double scale, @NotNull Vector3d pos, @NotNull Vector3d rot) {
        super(world, scale, pos, rot);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        this.getBulletObject().setGravity(MathHelper.convert(new Vector3d(0.0d, 2.0d, 0.0d)));
    }

    protected int lifeTime() {
        return 120;
    }
}
