package javagems3d.graphics.particles;

import javagems3d.graphics.particles.objects.base.ParticleFX;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;

public interface IParticlesEmitter {
    void onUpdateParticles(double frameDeltaTime, IWorld iWorld);

    void emitParticle(ParticleFX particleFX);

    void create(SceneWorld sceneWorld);

    void destroy(SceneWorld sceneWorld);
}
