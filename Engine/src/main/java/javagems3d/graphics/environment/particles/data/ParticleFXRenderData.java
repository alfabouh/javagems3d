package javagems3d.graphics.environment.particles.data;

import javagems3d.graphics.environment.particles.data.material.ParticleFXMaterial;
import javagems3d.graphics.environment.particles.data.material.ParticleFXProperties;
import javagems3d.graphics.environment.particles.data.material.ParticleFXSpriteProperties;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public record ParticleFXRenderData(@NotNull JGemsShaderManager transparencyShaderManager, @NotNull JGemsShaderManager mainSceneShaderManager, @NotNull ParticleFXProperties particleFXProperties, @NotNull ParticleFXMaterial particleFXMaterial, @NotNull ParticleFXSpriteProperties spriteProperties) {
    public boolean isTransparent() {
        return this.particleFXMaterial().getDiffuseColor().color().w < 1.0f;
    }

    public static ParticleFXRenderData DEFAULT(@NotNull JGemsShaderManager transparencyShaderManager, @NotNull JGemsShaderManager mainSceneShaderManager, @NotNull ParticleFXSpriteProperties particleFXSpriteProperties, @NotNull ParticleFXMaterial particleFXMaterial) {
        return new ParticleFXRenderData(
                transparencyShaderManager,
                mainSceneShaderManager,
                new ParticleFXProperties(0.0f, 0.0f),
                particleFXMaterial,
                particleFXSpriteProperties
        );
    }

    public static ParticleFXRenderData DEFAULT(@NotNull JGemsShaderManager transparencyShaderManager, @NotNull JGemsShaderManager mainSceneShaderManager, @NotNull ImageTexture texture, @NotNull ParticleFXSpriteProperties particleFXSpriteProperties) {
        return new ParticleFXRenderData(
                transparencyShaderManager,
                mainSceneShaderManager,
                new ParticleFXProperties(0.0f, 0.0f),
                new ParticleFXMaterial(texture, new Color4Texture(1f, 1f, 1f, 0.99f)),
                particleFXSpriteProperties
        );
    }

    public static ParticleFXRenderData DEFAULT(@NotNull JGemsShaderManager transparencyShaderManager, @NotNull JGemsShaderManager mainSceneShaderManager, @NotNull ParticleFXMaterial particleFXMaterial, @NotNull Vector2i cells, int maxSprites) {
        return new ParticleFXRenderData(
                transparencyShaderManager,
                mainSceneShaderManager,
                new ParticleFXProperties(0.0f, 0.0f),
                particleFXMaterial,
                new ParticleFXSpriteProperties(cells, maxSprites, true, 5.0f, true, false)
        );
    }

    public static ParticleFXRenderData DEFAULT(@NotNull JGemsShaderManager transparencyShaderManager, @NotNull JGemsShaderManager mainSceneShaderManager, @NotNull ImageTexture texture, @NotNull Vector2i cells, int maxSprites) {
        return new ParticleFXRenderData(
                transparencyShaderManager,
                mainSceneShaderManager,
                new ParticleFXProperties(0.0f, 0.0f),
                new ParticleFXMaterial(texture, new Color4Texture(1f, 1f, 1f, 0.99f)),
                new ParticleFXSpriteProperties(cells, maxSprites, true, 5.0f, true, false)
        );
    }
}