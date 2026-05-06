package javagems3d.graphics.environment.particles.data.material;

import javagems3d.system.resources.assets.texturing.colors.Color3Texture;
import javagems3d.system.resources.assets.texturing.colors.Color4Texture;
import javagems3d.system.resources.assets.texturing.maps.ImageTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class ParticleFXMaterial {
    private final ImageTexture textureMap;
    private final Color4Texture diffuseColor;
    private final Color3Texture emissionColor;

    public ParticleFXMaterial(@NotNull ImageTexture textureMap) {
        this(textureMap, new Color4Texture(new Vector4f(1f)), new Color3Texture(new Vector3f()));
    }

    public ParticleFXMaterial(@NotNull ImageTexture textureMap, float transparency) {
        this(textureMap, new Color4Texture(new Vector4f(1.0f, 1.0f, 1.0f, transparency)), new Color3Texture(new Vector3f()));
    }

    public ParticleFXMaterial(@NotNull ImageTexture textureMap, @NotNull Color4Texture diffuseColor) {
        this(textureMap, diffuseColor, new Color3Texture(new Vector3f()));
    }

    public ParticleFXMaterial(@NotNull ImageTexture textureMap, @NotNull Color4Texture diffuseColor, @NotNull Color3Texture emissionColor) {
        this.textureMap = textureMap;
        this.diffuseColor = diffuseColor;
        this.emissionColor = emissionColor;
    }

    public ImageTexture getTextureMap() {
        return this.textureMap;
    }

    public Color4Texture getDiffuseColor() {
        return this.diffuseColor;
    }

    public Color3Texture getEmissionColor() {
        return this.emissionColor;
    }
}
