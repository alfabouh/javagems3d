package javagems3d.system.resources.assets.models.parsing.space;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class ParsedMaterialData {
    Vector4f diffuseColor = null;
    Vector3f emissionColor = null;

    String emissionMapPath = null;
    String normalsMapPath = null;
    String diffuseMapPath = null;
    String metallicRoughnessMapPath = null;

    float metallicFactor = 0.0f;
    float roughnessFactor = 1.0f;

    public ParsedMaterialData(@Nullable Vector4f diffuseColor, @Nullable Vector3f emissionColor, @Nullable String emissionMapPath, @Nullable String normalsMapPath, String diffuseMapPath, String metallicRoughnessMapPath, float metallicFactor, float roughnessFactor) {
        this.diffuseColor = diffuseColor;
        this.emissionColor = emissionColor;
        this.emissionMapPath = emissionMapPath;
        this.normalsMapPath = normalsMapPath;
        this.diffuseMapPath = diffuseMapPath;
        this.metallicRoughnessMapPath = metallicRoughnessMapPath;
        this.metallicFactor = metallicFactor;
        this.roughnessFactor = roughnessFactor;
    }

    public Vector4f getDiffuseColor() {
        return this.diffuseColor;
    }

    public Vector3f getEmissionColor() {
        return this.emissionColor;
    }

    public String getEmissionMapPath() {
        return this.emissionMapPath;
    }

    public String getNormalsMapPath() {
        return this.normalsMapPath;
    }

    public String getDiffuseMapPath() {
        return this.diffuseMapPath;
    }

    public String getMetallicRoughnessMapPath() {
        return this.metallicRoughnessMapPath;
    }

    public float getMetallicFactor() {
        return this.metallicFactor;
    }

    public float getRoughnessFactor() {
        return this.roughnessFactor;
    }
}
