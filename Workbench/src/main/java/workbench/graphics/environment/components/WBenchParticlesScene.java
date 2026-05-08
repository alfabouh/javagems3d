package workbench.graphics.environment.components;

import javagems3d.graphics.environment.IEnvironment;
import javagems3d.graphics.environment.particles.IParticlesManager;
import javagems3d.graphics.environment.particles.scene.ParticlesScene;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import workbench.resources.WBenchResourceManager;

public class WBenchParticlesScene extends ParticlesScene {
    public WBenchParticlesScene(@NotNull IEnvironment environment, @NotNull IParticlesManager particlesManager) {
        super(environment, particlesManager);
    }

    @Override
    protected ShaderStorageBufferObject getParticlesIndirectSSBO() {
        return WBenchResourceManager.localShaderAssets.ParticleSceneIndirectBufferData;
    }

    @Override
    protected ShaderStorageBufferObject getParticlesPropertiesSBO() {
        return WBenchResourceManager.localShaderAssets.ParticleScenePropertiesData;
    }

    @Override
    public void update(IRenderWorld renderWorld) {
        this.getParticlesManager().update(renderWorld);
    }
}
