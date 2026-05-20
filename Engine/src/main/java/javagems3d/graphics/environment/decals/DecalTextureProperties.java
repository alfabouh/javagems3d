package javagems3d.graphics.environment.decals;

public class DecalTextureProperties {
    private float transparency;

    public DecalTextureProperties(float transparency) {
        this.transparency = transparency;
    }

    public float getTransparency() {
        return this.transparency;
    }


    public DecalTextureProperties setTransparency(float transparency) {
        this.transparency = transparency;
        return this;
    }
}
