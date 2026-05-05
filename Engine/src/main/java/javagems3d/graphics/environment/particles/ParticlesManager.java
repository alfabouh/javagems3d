package javagems3d.graphics.environment.particles;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.data.ParticleFXRenderData;
import javagems3d.graphics.environment.particles.data.material.ParticleFXMaterial;
import javagems3d.graphics.environment.particles.data.material.ParticleFXProperties;
import javagems3d.graphics.environment.particles.fx.ParticleFX;
import javagems3d.graphics.environment.particles.fx.SimpleParticleFX;
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
import java.util.List;

public class ParticlesManager implements IParticlesManager {
    private final List<ParticleFX> particlesFXList;

    public ParticlesManager() {
        this.particlesFXList = new ArrayList<>();

        this.particlesFXList.add(new SimpleParticleFX(new ParticleFXRenderData(
                JGemsResourceManager.globalShaderAssets.world_particle_oit,
                JGemsResourceManager.globalShaderAssets.world_particle,
                new ParticleFXProperties(0.0f, 0.0f),
                new ParticleFXMaterial(JGemsResourceManager.globalTextureAssets.defaultParticle, new Vector2i(1, 1))
        )).setPosition(new Vector3f(0f, 1f, 0f)));

        this.particlesFXList.add(new SimpleParticleFX(new ParticleFXRenderData(
                JGemsResourceManager.globalShaderAssets.world_particle_oit,
                JGemsResourceManager.globalShaderAssets.world_particle,
                new ParticleFXProperties(0.0f, 0.0f),
                new ParticleFXMaterial((ImageTexture) JGemsResourceManager.globalTextureAssets.waterTexture, new Vector2i(1, 1))
        )).setPosition(new Vector3f(0f, 2f, 0f)));

        this.particlesFXList.add(new SimpleParticleFX(new ParticleFXRenderData(
                JGemsResourceManager.globalShaderAssets.world_particle_oit,
                JGemsResourceManager.globalShaderAssets.world_particle,
                new ParticleFXProperties(0.0f, 0.0f),
                new ParticleFXMaterial(JGemsResourceManager.globalTextureAssets.defaultParticle, new Vector2i(1, 1))
        )).setPosition(new Vector3f(0f, 3f, 0f)));
    }

    @Override
    public void update(IRenderWorld renderWorld) {
        this.getParticlesFXList().forEach(particleFX -> particleFX.update(renderWorld));

        if (((DefaultBindings) JGemsHelper.controller().getBindingManager()).keyX.isClicked()) {
            this.particlesFXList.add(new SimpleParticleFX(new ParticleFXRenderData(
                    JGemsResourceManager.globalShaderAssets.world_particle_oit,
                    JGemsResourceManager.globalShaderAssets.world_particle,
                    new ParticleFXProperties(0.0f, 0.5f),
                    new ParticleFXMaterial(JGemsResourceManager.globalTextureAssets.defaultParticle, new Color4Texture(1f, 1f, 1f, 1f), new Vector2i(1, 1))
            )).setPosition(new Vector3f(JGemsHelper.math().calcLookVector(renderWorld.getCamera().getCamRotation()).mul(5f).add(renderWorld.getCamera().getCamPosition()))));
        }
    }

    public void addParticleFX(ParticleFX particleFX) {
        this.particlesFXList.add(particleFX);
    }

    public List<ParticleFX> getParticlesFXList() {
        return this.particlesFXList;
    }
}
