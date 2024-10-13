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

package javagems3d.graphics.opengl.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.particles.attributes.ParticleAttributes;
import javagems3d.graphics.opengl.particles.objects.SimpleColoredParticle;
import javagems3d.graphics.opengl.particles.objects.SimpleTexturedParticle;
import javagems3d.graphics.opengl.particles.objects.base.ParticleFX;
import javagems3d.graphics.opengl.rendering.JGemsSceneGlobalConstants;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneData;
import javagems3d.graphics.opengl.world.SceneWorld;
import javagems3d.physics.world.IWorld;
import javagems3d.system.resources.assets.material.samples.packs.ParticleTexturePack;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;
import javagems3d.system.service.synchronizing.SyncManager;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public final class ParticlesEmitter implements IParticlesEmitter {
    private final Set<ParticleFX> particlesSet;
    private Model<Format3D> commonParticleModel2D;

    public ParticlesEmitter() {
        this.particlesSet = SyncManager.createSyncronisedSet();
    }

    public static SimpleTexturedParticle createSimpleTexturedParticle(SceneWorld sceneWorld, ParticleAttributes particleAttributes, ParticleTexturePack particleTexturePack, Vector3f pos, Vector2f scaling) {
        return new SimpleTexturedParticle(sceneWorld, particleAttributes, particleTexturePack, pos, scaling);
    }

    public static SimpleColoredParticle createSimpleColoredParticle(SceneWorld world, ParticleAttributes particleAttributes, Vector3f color, Vector3f pos, Vector2f scaling) {
        return new SimpleColoredParticle(world, particleAttributes, color, pos, scaling);
    }

    public void onUpdateParticles(double frameDeltaTime, IWorld iWorld) {
        Iterator<ParticleFX> particleFXIterator = this.getParticlesSet().iterator();
        while (particleFXIterator.hasNext()) {
            ParticleFX particleFX = particleFXIterator.next();
            if (particleFX.isDead()) {
                particleFX.onDestroy(iWorld);
                particleFXIterator.remove();
                continue;
            }
            particleFX.onUpdateParticle(frameDeltaTime, iWorld);
        }
    }

    @Override
    public void emitParticle(ParticleFX particleFX) {
        this.getParticlesSet().add(particleFX);
        if (this.getParticlesSet().size() > JGemsSceneGlobalConstants.MAX_PARTICLES) {
            this.getParticlesSet().remove(this.getParticlesSet().stream().findFirst().get());
        }
    }

    @Override
    public void create(SceneWorld sceneWorld) {
        final float size = 0.1f;
        this.commonParticleModel2D = MeshHelper.generatePlane3DModel(new Vector3f(-size, -size, 0.0f), new Vector3f(size, -size, 0.0f), new Vector3f(-size, size, 0.0f), new Vector3f(size, size, 0.0f));
        JGemsHelper.getLogger().log("Created particles emitter!");
    }

    @Override
    public void destroy(SceneWorld sceneWorld) {
        this.commonParticleModel2D.clean();
        this.cleanParticles(sceneWorld);
        JGemsHelper.getLogger().log("Destroyed particles emitter!");
    }

    public void cleanParticles(SceneWorld sceneWorld) {
        this.getParticlesSet().forEach(e -> e.onDestroy(sceneWorld));
        this.getParticlesSet().clear();
    }

    public Model<Format3D> getParticleModel(ParticleFX particleFX) {
        return new Model<>(this.commonParticleModel2D, new Format3D(particleFX.getPosition(), new Vector3f(0.0f), new Vector3f(particleFX.getScaling(), 1.0f)).setOrientedToView(true));
    }

    public Set<ParticleFX> getCulledParticlesSet(SceneData sceneData) {
        return sceneData.getSceneWorld().getCollectionFrustumCulledList(this.getParticlesSet()).stream().map(e -> (ParticleFX) e).filter(e -> e.getPosition().distance(sceneData.getCamera().getCamPosition()) <= e.getParticleAttributes().getDistanceToRender()).collect(Collectors.toSet());
    }

    public Set<ParticleFX> getParticlesSet() {
        return this.particlesSet;
    }
}