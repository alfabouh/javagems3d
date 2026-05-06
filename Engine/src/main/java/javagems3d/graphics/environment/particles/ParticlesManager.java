package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.data.ParticleFXRenderData;
import javagems3d.graphics.environment.particles.data.material.ParticleFXMaterial;
import javagems3d.graphics.environment.particles.data.material.ParticleFXProperties;
import javagems3d.graphics.environment.particles.data.material.ParticleFXSpriteProperties;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.environment.particles.fx.SimpleParticleFX;
import javagems3d.graphics.environment.particles.fx.WorldDefaultParticleFX;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.help.JGemsHelper;
import javagems3d.system.controller.binding.DefaultBindings;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParticlesManager implements IParticlesManager {
    private final IEnvironment environment;
    private final List<ParticleFX> particlesFXList;

    public ParticlesManager(IEnvironment environment) {
        this.environment = environment;
        this.particlesFXList = new ArrayList<>();
    }

    @Override
    public void update(IRenderWorld renderWorld) {
        Iterator<ParticleFX> iterator = this.getParticlesFXList().iterator();
        while (iterator.hasNext()) {
            ParticleFX particle = iterator.next();
            if (particle.isDead()) {
                particle.onDestroy(renderWorld);
                iterator.remove();
            } else {
                particle.onUpdate(renderWorld);
            }
        }

        if (((DefaultBindings) JGemsHelper.controller().getBindingManager()).keyX.isClicked()) {
            this.spawnParticleFX(new WorldDefaultParticleFX(new ParticleFXRenderData(
                    JGemsResourceManager.globalShaderAssets.world_particle_oit,
                    JGemsResourceManager.globalShaderAssets.world_particle,
                    new ParticleFXProperties(0.0f, 0.0f),
                    new ParticleFXMaterial(JGemsResourceManager.globalTextureAssets.defaultParticle, new Color4Texture(1f, 1f, 1f, 0.5f)),
                    new ParticleFXSpriteProperties(new Vector2i(7, 7), 46, true, 0.1f)
            ), 0.0f, new Vector3f(), new Vector3f()).setScaling(new Vector3f(5f)).setPosition(new Vector3f(JGemsHelper.math().calcLookVector(renderWorld.getCamera().getCamRotation()).mul(5f).add(renderWorld.getCamera().getCamPosition()))));
        }
    }

    public void spawnParticleFX(ParticleFX particleFX) {
        particleFX.onSpawn(this.getEnvironment().getWorld());
        this.particlesFXList.add(particleFX);
    }

    public IEnvironment getEnvironment() {
        return this.environment;
    }

    public List<ParticleFX> getParticlesFXList() {
        return this.particlesFXList;
    }
}
