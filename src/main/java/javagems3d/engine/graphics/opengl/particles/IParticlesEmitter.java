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

package javagems3d.engine.graphics.opengl.particles;

import javagems3d.engine.graphics.opengl.particles.objects.base.ParticleFX;
import javagems3d.engine.graphics.opengl.world.SceneWorld;
import javagems3d.engine.physics.world.IWorld;

public interface IParticlesEmitter {
    void onUpdateParticles(double frameDeltaTime, IWorld iWorld);

    void emitParticle(ParticleFX particleFX);

    void create(SceneWorld sceneWorld);

    void destroy(SceneWorld sceneWorld);
}
