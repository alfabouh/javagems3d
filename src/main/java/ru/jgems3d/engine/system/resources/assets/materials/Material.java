package ru.jgems3d.engine.system.resources.assets.materials;

import org.joml.Vector4f;
import ru.jgems3d.engine.system.resources.assets.materials.samples.ColorSample;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.ISample;

public class Material {
    private boolean useInTransparencyPass;
    
    private ISample diffuse;
    private ISample opacity;
    private IImageSample normals;
    private IImageSample emissive;
    private IImageSample specular;
    private IImageSample metallic;

    public Material() {
        this.setDefaults();
    }

    public static Material createDefault() {
        return new Material();
    }

    public void setDefaults() {
        this.setDefaultDiffuse();
        this.setDefaultEmissive();
        this.setDefaultNormals();
        this.setDefaultSpecular();
        this.setDefaultMetallic();
        this.setDefaultOpacity();
        this.useInTransparencyPass = false;
    }

    public void setUseInTransparencyPass(boolean useInTransparencyPass) {
        this.useInTransparencyPass = useInTransparencyPass;
    }

    public boolean isUseInTransparencyPass() {
        return this.useInTransparencyPass;
    }

    public ISample getOpacity() {
        return this.opacity;
    }

    public void setOpacity(ISample opacity) {
        this.opacity = opacity;
    }

    public ISample getDiffuse() {
        return this.diffuse;
    }

    public void setDiffuse(ISample diffuse) {
        this.diffuse = diffuse;
    }

    public IImageSample getEmissive() {
        return this.emissive;
    }

    public void setEmissive(IImageSample emissive) {
        this.emissive = emissive;
    }

    public IImageSample getMetallic() {
        return this.metallic;
    }

    public void setMetallic(IImageSample metallic) {
        this.metallic = metallic;
    }

    public IImageSample getNormals() {
        return this.normals;
    }

    public void setNormals(IImageSample normals) {
        this.normals = normals;
    }

    public IImageSample getSpecular() {
        return this.specular;
    }

    public void setSpecular(IImageSample specular) {
        this.specular = specular;
    }

    public void setDefaultMetallic() {
        this.metallic = null;
    }

    public void setDefaultDiffuse() {
        this.diffuse = ColorSample.createColor(new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));
    }

    public void setDefaultNormals() {
        this.normals = null;
    }

    public void setDefaultEmissive() {
        this.emissive = null;
    }

    public void setDefaultSpecular() {
        this.specular = null;
    }

    public void setDefaultOpacity() {
        this.opacity = null;
    }
}