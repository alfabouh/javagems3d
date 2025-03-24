package javagems3d.system.resources.assets.models.parsing.space;

import org.joml.Vector3f;
import org.joml.Vector4f;

public final class ParsedMaterialsData {
    Vector4f diffuseColor = null;
    Vector3f emissionColor = null;

    String emissionTexture = null;
    String normalsTexture = null;
    String diffuseTexture = null;
    String metallicRoughnessTexture = null;

    float metallicFactor = 0.0f;
    float roughnessFactor = 1.0f;

    public ParsedMaterialsData(Vector4f diffuseColor, Vector3f emissionColor, String emissionTexture, String normalsTexture, String diffuseTexture, String metallicRoughnessTexture, float metallicFactor, float roughnessFactor) {
        this.diffuseColor = diffuseColor;
        this.emissionColor = emissionColor;
        this.emissionTexture = emissionTexture;
        this.normalsTexture = normalsTexture;
        this.diffuseTexture = diffuseTexture;
        this.metallicRoughnessTexture = metallicRoughnessTexture;
        this.metallicFactor = metallicFactor;
        this.roughnessFactor = roughnessFactor;
    }

    public Vector4f getDiffuseColor() {
        return this.diffuseColor;
    }

    public Vector3f getEmissionColor() {
        return this.emissionColor;
    }

    public String getEmissionTexture() {
        return this.emissionTexture;
    }

    public String getNormalsTexture() {
        return this.normalsTexture;
    }

    public String getDiffuseTexture() {
        return this.diffuseTexture;
    }

    public String getMetallicRoughnessTexture() {
        return this.metallicRoughnessTexture;
    }

    public float getMetallicFactor() {
        return this.metallicFactor;
    }

    public float getRoughnessFactor() {
        return this.roughnessFactor;
    }
}
