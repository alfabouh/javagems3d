package javagems3d.system.resources.assets.materials;

import javagems3d.system.resources.assets.texturing.RGBAColor;
import javagems3d.system.resources.assets.texturing.base.ISample;
import javagems3d.system.resources.assets.texturing.base.ImageBasedTexture;
import org.joml.Vector4f;

@SuppressWarnings("all")
public class Material {
    private int id;
    private float fullOpacity;
    private ISample diffuse;
    private ImageBasedTexture opacityMap;
    private ImageBasedTexture normalsMap;
    private ImageBasedTexture emissionMap;
    private ImageBasedTexture specularMap;
    private ImageBasedTexture metallicMap;

    public Material(ISample diffuse) {
        this();
        this.setDefaults().setDiffuse(diffuse);
    }

    public Material() {
        this.setDefaults();
        this.id = 0;
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

    public int getId() {
        return this.id;
    }

    public Material setId(int id) {
        this.id = id;
        return this;
    }

    public Material setDefaultMetallic() {
        this.metallicMap = null;
        return this;
    }

    public Material setDefaultDiffuse() {
        this.diffuse = new RGBAColor(new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));
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

    public ImageBasedTexture getEmissionMap() {
        return this.emissionMap;
    }

    public Material setEmissionMap(ImageBasedTexture emissionMap) {
        this.emissionMap = emissionMap;
        return this;
    }

    public ImageBasedTexture getMetallicMap() {
        return this.metallicMap;
    }

    public Material setMetallicMap(ImageBasedTexture metallicMap) {
        this.metallicMap = metallicMap;
        return this;
    }

    public ImageBasedTexture getNormalsMap() {
        return this.normalsMap;
    }

    public Material setNormalsMap(ImageBasedTexture normalsMap) {
        this.normalsMap = normalsMap;
        return this;
    }

    public ImageBasedTexture getSpecularMap() {
        return this.specularMap;
    }

    public Material setSpecularMap(ImageBasedTexture specularMap) {
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

    public Material setOpacityMap(ImageBasedTexture opacityMap) {
        this.opacityMap = opacityMap;
        return this;
    }

    public float getFullOpacity() {
        float w1 = 1.0f;
        if (this.getDiffuse() instanceof RGBAColor) {
            w1 = ((RGBAColor) (this.getDiffuse())).getColor().w;
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
