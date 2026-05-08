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

    public static ParticleFXRenderData DEFAULT(ParticleFXSpriteProperties particleFXSpriteProperties, ParticleFXMaterial particleFXMaterial, Vector2i cells, int maxSprites) {
        return new ParticleFXRenderData(
                JGemsResourceManager.globalShaderAssets.world_particle_oit,
                JGemsResourceManager.globalShaderAssets.world_particle,
                new ParticleFXProperties(0.0f, 0.0f),
                particleFXMaterial,
                particleFXSpriteProperties
        );
    }

    public static ParticleFXRenderData DEFAULT(ImageTexture texture, ParticleFXSpriteProperties particleFXSpriteProperties, Vector2i cells, int maxSprites) {
        return new ParticleFXRenderData(
                JGemsResourceManager.globalShaderAssets.world_particle_oit,
                JGemsResourceManager.globalShaderAssets.world_particle,
                new ParticleFXProperties(0.0f, 0.0f),
                new ParticleFXMaterial(texture, new Color4Texture(1f, 1f, 1f, 0.99f)),
                particleFXSpriteProperties
        );
    }

    public static ParticleFXRenderData DEFAULT(ParticleFXMaterial particleFXMaterial, Vector2i cells, int maxSprites) {
        return new ParticleFXRenderData(
                JGemsResourceManager.globalShaderAssets.world_particle_oit,
                JGemsResourceManager.globalShaderAssets.world_particle,
                new ParticleFXProperties(0.0f, 0.0f),
                particleFXMaterial,
                new ParticleFXSpriteProperties(cells, maxSprites, true, 0.1f)
        );
    }

    public static ParticleFXRenderData DEFAULT(ImageTexture texture, Vector2i cells, int maxSprites) {
        return new ParticleFXRenderData(
                JGemsResourceManager.globalShaderAssets.world_particle_oit,
                JGemsResourceManager.globalShaderAssets.world_particle,
                new ParticleFXProperties(0.0f, 0.0f),
                new ParticleFXMaterial(texture, new Color4Texture(1f, 1f, 1f, 0.99f)),
                new ParticleFXSpriteProperties(cells, maxSprites, true, 0.1f)
        );
    }
}