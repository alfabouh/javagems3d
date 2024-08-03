package ru.jgems3d.engine.system.resources.assets.materials;

import org.joml.Vector4f;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ColorSample;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.ISample;

public class Material {
    private float fullOpacity;
    private ISample diffuse;
    private IImageSample opacityMap;
    private IImageSample normalsMap;
    private IImageSample emissionMap;
    private IImageSample specularMap;
    private IImageSample metallicMap;

    public Material() {
        this.setDefaults();
    }

    public static Material createDefault() {
        return new Material();
    }

    public void setDefaults() {
        this.setDefaultDiffuse();
        this.setDefaultEmission();
        this.setDefaultNormals();
        this.setDefaultSpecular();
        this.setDefaultMetallic();
        this.setDefaultOpacity();
        this.setFullOpacity(1.0f);
    }

    public void setFullOpacity(float fullOpacity) {
        this.fullOpacity = fullOpacity;
    }

    public float getFullOpacity() {
        float w1 = 1.0f;
        if (this.getDiffuse() instanceof ColorSample) {
            w1 = ((ColorSample) (this.getDiffuse())).getColor().w;
        }
        return this.fullOpacity * w1;
    }

    public ISample getOpacityMap() {
        return this.opacityMap;
    }

    public void setOpacityMap(IImageSample opacityMap) {
        this.opacityMap = opacityMap;
    }

    public ISample getDiffuse() {
        return this.diffuse;
    }

    public void setDiffuse(ISample diffuse) {
        this.diffuse = diffuse;
    }

    public IImageSample getEmissionMap() {
        return this.emissionMap;
    }

    public void setEmissionMap(IImageSample emissionMap) {
        this.emissionMap = emissionMap;
    }

    public IImageSample getMetallicMap() {
        return this.metallicMap;
    }

    public void setMetallicMap(IImageSample metallicMap) {
        this.metallicMap = metallicMap;
    }

    public IImageSample getNormalsMap() {
        return this.normalsMap;
    }

    public void setNormalsMap(IImageSample normalsMap) {
        this.normalsMap = normalsMap;
    }

    public IImageSample getSpecularMap() {
        return this.specularMap;
    }

    public void setSpecularMap(IImageSample specularMap) {
        this.specularMap = specularMap;
    }

    public void setDefaultMetallic() {
        this.metallicMap = null;
    }

    public void setDefaultDiffuse() {
        this.diffuse = ColorSample.createColor(new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));
    }

    public void setDefaultNormals() {
        this.normalsMap = null;
    }

    public void setDefaultEmission() {
        this.emissionMap = null;
    }

    public void setDefaultSpecular() {
        this.specularMap = null;
    }

    public void setDefaultOpacity() {
        this.opacityMap = null;
    }

    public boolean hasTransparency() {
        return this.getFullOpacity() < 1.0f || this.getOpacityMap() != null;
    }
}