package javagems3d.system.resources.assets.loading.models.gltf.parsing.structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class GLTF2Material {
    private final String name;
    private int matFlags;

    public final static int DIFFUSION_COLOR = 1 << 2;
    public final static int DIFFUSION_TEXTURE = 1 << 3;
    public final static int NORMALS_TEXTURE = 1 << 4;
    public final static int EMISSION_COLOR = 1 << 5;
    public final static int EMISSION_TEXTURE = 1 << 6;
    public final static int METALLIC_FACTOR = 1 << 7;
    public final static int ROUGHNESS_FACTOR = 1 << 8;
    public final static int METALLIC_ROUGHNESS_TEXTURE = 1 << 9;
    public final static int OPACITY = 1 << 10;

    private Vector4f diffusionColor;
    private GLTF2ImageTexture diffusionTexture;

    private GLTF2ImageTexture normalTexture;

    private Vector3f emissionColor;
    private GLTF2ImageTexture emissionTexture;

    private float metallicFactor;
    private float roughnessFactor;
    private GLTF2ImageTexture metallicRoughnessTexture;
    private float opacity;

    public GLTF2Material(String name) {
        this.matFlags = 0;
        this.name = name;
    }

    public boolean hasFlag(int flag) {
        return (this.matFlags & flag) != 0;
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    public GLTF2Material setDiffusionColor(@NotNull Vector4f diffusionColor) {
        this.diffusionColor = diffusionColor;
        this.matFlags |= GLTF2Material.DIFFUSION_COLOR;
        return this;
    }

    public GLTF2Material setDiffusionTextureIndex(GLTF2ImageTexture diffusionTexture) {
        this.diffusionTexture = diffusionTexture;
        this.matFlags |= GLTF2Material.DIFFUSION_TEXTURE;
        return this;
    }

    public GLTF2Material setNormalTextureIndex(GLTF2ImageTexture normalTexture) {
        this.normalTexture = normalTexture;
        this.matFlags |= GLTF2Material.NORMALS_TEXTURE;
        return this;
    }

    public GLTF2Material setEmissionColor(@NotNull Vector3f emissionColor) {
        this.emissionColor = emissionColor;
        this.matFlags |= GLTF2Material.EMISSION_COLOR;
        return this;
    }

    public GLTF2Material setEmissionTexture(GLTF2ImageTexture emissionTexture) {
        this.emissionTexture = emissionTexture;
        this.matFlags |= GLTF2Material.EMISSION_TEXTURE;
        return this;
    }

    public GLTF2Material setMetallicFactor(float metallicFactor) {
        this.metallicFactor = metallicFactor;
        this.matFlags |= GLTF2Material.METALLIC_FACTOR;
        return this;
    }

    public GLTF2Material setRoughnessFactor(float roughnessFactor) {
        this.roughnessFactor = roughnessFactor;
        this.matFlags |= GLTF2Material.ROUGHNESS_FACTOR;
        return this;
    }

    public GLTF2Material setMetallicRoughnessTexture(GLTF2ImageTexture metallicRoughnessTexture) {
        this.metallicRoughnessTexture = metallicRoughnessTexture;
        this.matFlags |= GLTF2Material.METALLIC_ROUGHNESS_TEXTURE;
        return this;
    }

    public GLTF2Material setOpacity(float opacity) {
        this.opacity = opacity;
        this.matFlags |= GLTF2Material.OPACITY;
        return this;
    }

    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

    public @Nullable Vector4f getDiffusionColor() {
        return this.diffusionColor;
    }

    public GLTF2ImageTexture getDiffusionTexture() {
        return this.diffusionTexture;
    }

    public GLTF2ImageTexture getNormalTexture() {
        return this.normalTexture;
    }

    public @Nullable Vector3f getEmissionColor() {
        return this.emissionColor;
    }

    public GLTF2ImageTexture getEmissionTexture() {
        return this.emissionTexture;
    }

    public float getMetallicFactor() {
        return this.metallicFactor;
    }

    public float getRoughnessFactor() {
        return this.roughnessFactor;
    }

    public GLTF2ImageTexture getMetallicRoughnessTexture() {
        return this.metallicRoughnessTexture;
    }

    public float getOpacity() {
        return this.opacity;
    }
}
