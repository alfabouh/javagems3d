package javagems3d.graphics.environment.lights.scene;

public interface IHasSSAO {
    float getSsaoRange();
    void setSsaoRange(float ssaoRange);
    float getSsaoBias();
    void setSsaoBias(float ssaoBias);
    float getSsaoRadius();
    void setSsaoRadius(float ssaoRadius);
}
