package javagems3d.system.resources.assets.materials;

import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.system.resources.assets.texturing.Color4Texture;
import javagems3d.system.resources.assets.texturing.base.ISample;
import org.joml.Vector4f;

@SuppressWarnings("all")
public class Material {
    private float fullOpacity;
    private ISample diffuse;
    private ITexture2DProgram opacityMap;
    private ITexture2DProgram normalsMap;
    private ITexture2DProgram emissionMap;
    private ITexture2DProgram specularMap;
    private ITexture2DProgram metallicMap;

    public Material(ISample diffuse) {
        this();
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
        this.diffuse = new Color4Texture(new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));
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

    public ITexture2DProgram getEmissionMap() {
        return this.emissionMap;
    }

    public Material setEmissionMap(ITexture2DProgram emissionMap) {
        this.emissionMap = emissionMap;
        return this;
    }

    public ITexture2DProgram getMetallicMap() {
        return this.metallicMap;
    }

    public Material setMetallicMap(ITexture2DProgram metallicMap) {
        this.metallicMap = metallicMap;
        return this;
    }

    public ITexture2DProgram getNormalsMap() {
        return this.normalsMap;
    }

    public Material setNormalsMap(ITexture2DProgram normalsMap) {
        this.normalsMap = normalsMap;
        return this;
    }

    public ITexture2DProgram getSpecularMap() {
        return this.specularMap;
    }

    public Material setSpecularMap(ITexture2DProgram specularMap) {
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

    public ITexture2DProgram getOpacityMap() {
        return this.opacityMap;
    }

    public Material setOpacityMap(ITexture2DProgram opacityMap) {
        this.opacityMap = opacityMap;
        return this;
    }

    public float getFullOpacity() {
        float w1 = 1.0f;
        if (this.getDiffuse() instanceof Color4Texture) {
            w1 = ((Color4Texture) (this.getDiffuse())).getColor().w;
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
