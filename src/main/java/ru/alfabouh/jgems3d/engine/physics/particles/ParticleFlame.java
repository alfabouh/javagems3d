package ru.alfabouh.jgems3d.engine.physics.particles;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.math.MathHelper;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.World;

public class ParticleFlame extends SimpleParticle {


    public ParticleFlame(World world, Vector3f scale, @NotNull Vector3f pos, @NotNull Vector3f rot) {
        super(world, scale, pos, rot);
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
        this.getBulletObject().setGravity(MathHelper.convert(new Vector3f(0.0f, 2.0f, 0.0f)));
    }

    protected int lifeTime() {
        return 120;
    }
}
