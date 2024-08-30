/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine.system.resources.assets.material;

import org.joml.Vector4f;
import ru.jgems3d.engine.system.resources.assets.material.samples.ColorSample;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ISample;
import ru.jgems3d.engine.system.resources.assets.material.samples.base.ITextureSample;

@SuppressWarnings("all")
public class Material {
    private float fullOpacity;
    private ISample diffuse;
    private ITextureSample opacityMap;
    private ITextureSample normalsMap;
    private ITextureSample emissionMap;
    private ITextureSample specularMap;
    private ITextureSample metallicMap;

    public Material(ISample diffuse) {
        this.setDefaults().setDiffuse(diffuse);
    }

    public Material() {
        this.setDefaults();
    }

    public static Material createDefault() {
        return new Material();
    }

    public Material setDefaults() {
        this.setDefaultDiffuse();
        this.setDefaultEmission();
        this.setDefaultNormals();
        this.setDefaultSpecular();
        this.setDefaultMetallic();
        this.setDefaultOpacity();
        this.setFullOpacity(1.0f);
        return this;
    }

    public Material setDefaultMetallic() {
        this.metallicMap = null;
        return this;
    }

    public Material setDefaultDiffuse() {
        this.diffuse = ColorSample.createColor(new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));
        return this;
    }

    public Material setDefaultNormals() {
        this.normalsMap = null;
        return this;
    }

    public Material setDefaultEmission() {
        this.emissionMap = null;
        return this;
    }

    public Material setDefaultSpecular() {
        this.specularMap = null;
        return this;
    }

    public Material setDefaultOpacity() {
        this.opacityMap = null;
        return this;
    }

    public ITextureSample getEmissionMap() {
        return this.emissionMap;
    }

    public Material setEmissionMap(ITextureSample emissionMap) {
        this.emissionMap = emissionMap;
        return this;
    }

    public ITextureSample getMetallicMap() {
        return this.metallicMap;
    }

    public Material setMetallicMap(ITextureSample metallicMap) {
        this.metallicMap = metallicMap;
        return this;
    }

    public ITextureSample getNormalsMap() {
        return this.normalsMap;
    }

    public Material setNormalsMap(ITextureSample normalsMap) {
        this.normalsMap = normalsMap;
        return this;
    }

    public ITextureSample getSpecularMap() {
        return this.specularMap;
    }

    public Material setSpecularMap(ITextureSample specularMap) {
        this.specularMap = specularMap;
        return this;
    }

    public ISample getDiffuse() {
        return this.diffuse;
    }

    public Material setDiffuse(ISample diffuse) {
        this.diffuse = diffuse;
        return this;
    }

    public ISample getOpacityMap() {
        return this.opacityMap;
    }

    public Material setOpacityMap(ITextureSample opacityMap) {
        this.opacityMap = opacityMap;
        return this;
    }

    public float getFullOpacity() {
        float w1 = 1.0f;
        if (this.getDiffuse() instanceof ColorSample) {
            w1 = ((ColorSample) (this.getDiffuse())).getColor().w;
        }
        return this.fullOpacity * w1;
    }

    public Material setFullOpacity(float fullOpacity) {
        this.fullOpacity = fullOpacity;
        return this;
    }

    public boolean hasTransparency() {
        return this.getFullOpacity() < 1.0f || this.getOpacityMap() != null;
    }
}