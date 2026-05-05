package javagems3d.graphics.environment.particles.data;

import javagems3d.graphics.environment.particles.data.material.ParticleFXMaterial;
import javagems3d.graphics.environment.particles.data.material.ParticleFXProperties;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;

public record ParticleFXRenderData(@NotNull JGemsShaderManager transparencyShaderManager, @NotNull JGemsShaderManager mainSceneShaderManager, @NotNull ParticleFXProperties particleFXProperties, @NotNull ParticleFXMaterial particleFXMaterial) {
    public boolean isTransparent() {
        return this.particleFXMaterial().getDiffuseColor().color().w < 1.0f;
    }
}
