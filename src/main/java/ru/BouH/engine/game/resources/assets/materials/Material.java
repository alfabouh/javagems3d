package ru.BouH.engine.game.resources.assets.materials;

import org.joml.Vector4d;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.materials.textures.ColorSample;
import ru.BouH.engine.game.resources.assets.materials.textures.IImageSample;
import ru.BouH.engine.game.resources.assets.materials.textures.ISample;
import ru.BouH.engine.render.scene.programs.CubeMapProgram;

public class Material {
    private ISample diffuse;
    private IImageSample normals;
    private IImageSample emissive;
    private IImageSample specular;
    private IImageSample metallic;
    private CubeMapProgram ambientCubeMap;

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
        this.setDefaultCubeMap();
    }

    public CubeMapProgram getAmbientCubeMap() {
        return this.ambientCubeMap;
    }

    public void setAmbientCubeMap(CubeMapProgram ambientCubeMap) {
        this.ambientCubeMap = ambientCubeMap;
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
        this.diffuse = ColorSample.createColor(new Vector4d(1.0d, 0.0d, 1.0d, 1.0d));
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

    public void setDefaultCubeMap() {
        this.ambientCubeMap = ResourceManager.renderAssets.skyboxCubeMap;
    }
}