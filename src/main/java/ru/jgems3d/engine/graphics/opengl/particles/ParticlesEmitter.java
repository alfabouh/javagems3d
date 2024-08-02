package ru.jgems3d.engine.graphics.opengl.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.particles.attributes.ParticleAttributes;
import ru.jgems3d.engine.graphics.opengl.particles.objects.ParticleFX;
import ru.jgems3d.engine.graphics.opengl.particles.objects.SimpleParticle;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.IWorld;
import ru.jgems3d.engine.system.resources.assets.materials.samples.packs.ParticleTexturePack;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.basic.MeshHelper;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public final class ParticlesEmitter implements IParticlesEmitter {
    private final Set<ParticleFX> particlesSet;
    private Model<Format3D> commonParticleModel2D;

    public ParticlesEmitter() {
        this.particlesSet = new HashSet<>();
    }

    public static SimpleParticle createSimpleParticle(SceneWorld sceneWorld, ParticleAttributes particleAttributes, ParticleTexturePack particleTexturePack,  Vector3f pos, Vector2f scaling) {
        return new SimpleParticle(sceneWorld, particleAttributes, particleTexturePack, pos, scaling);
    }

    public void onUpdateParticles(double deltaTime, IWorld iWorld) {
        Iterator<ParticleFX> particleFXIterator = this.getParticlesSet().iterator();
        while (particleFXIterator.hasNext()) {
            ParticleFX particleFX = particleFXIterator.next();
            if (particleFX.isDead()) {
                particleFX.onDestroy(iWorld);
                particleFXIterator.remove();
                continue;
            }
            particleFX.onUpdateParticle(deltaTime, iWorld);
        }
    }

    @Override
    public void emitParticle(ParticleFX particleFX) {
        this.getParticlesSet().add(particleFX);
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