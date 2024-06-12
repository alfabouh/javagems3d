package ru.alfabouh.jgems3d.engine.physics.particles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import ru.alfabouh.jgems3d.engine.math.MathHelper;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;

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
