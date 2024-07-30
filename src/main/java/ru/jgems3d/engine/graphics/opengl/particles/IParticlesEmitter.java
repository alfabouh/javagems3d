package ru.jgems3d.engine.graphics.opengl.particles;

import ru.jgems3d.engine.graphics.opengl.particles.objects.ParticleFX;
import ru.jgems3d.engine.graphics.opengl.rendering.IScene;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.IWorld;

public interface IParticlesEmitter {
    void onUpdateParticles(double partialTicks, IWorld iWorld);
    void emitParticle(ParticleFX particleFX);
    void create(SceneWorld sceneWorld);
    void destroy(SceneWorld sceneWorld);
}
