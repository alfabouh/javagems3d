package javagems3d.system.resources.assets.models.parsing.space;

import org.joml.Vector3f;
import org.joml.Vector4f;

public final class ParsedMaterialsData {
    Vector4f diffuseColor = null;
    Vector3f emissionColor = null;

    String emissionMap = null;
    String normalsMap = null;
    String diffuseMap = null;
    String metallicRoughnessMap = null;

    float metallicFactor = 0.0f;
    float roughnessFactor = 1.0f;

    public ParsedMaterialsData(Vector4f diffuseColor, Vector3f emissionColor, String emissionMap, String normalsMap, String diffuseMap, String metallicRoughnessMap, float metallicFactor, float roughnessFactor) {
        this.diffuseColor = diffuseColor;
        this.emissionColor = emissionColor;
        this.emissionMap = emissionMap;
        this.normalsMap = normalsMap;
        this.diffuseMap = diffuseMap;
        this.metallicRoughnessMap = metallicRoughnessMap;
        this.metallicFactor = metallicFactor;
        this.roughnessFactor = roughnessFactor;
    }

    public Vector4f getDiffuseColor() {
        return this.diffuseColor;
    }

    public Vector3f getEmissionColor() {
        return this.emissionColor;
    }

    public String getEmissionMap() {
        return this.emissionMap;
    }

    public String getNormalsMap() {
        return this.normalsMap;
    }

    public String getDiffuseMap() {
        return this.diffuseMap;
    }

    public String getMetallicRoughnessMap() {
        return this.metallicRoughnessMap;
    }

    public float getMetallicFactor() {
        return this.metallicFactor;
    }

    public float getRoughnessFactor() {
        return this.roughnessFactor;
    }
}
