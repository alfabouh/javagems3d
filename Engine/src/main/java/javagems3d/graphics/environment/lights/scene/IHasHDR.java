package javagems3d.graphics.environment.lights.scene;

public interface IHasHDR {
    void setBloomEnabled(boolean bloom);
    void setHdrExposure(float exposure);
    void setHdrGamma(float gamma);
    boolean isBloomEnabled();
    float getHdrExposure();
    float getHdrGamma();
}
