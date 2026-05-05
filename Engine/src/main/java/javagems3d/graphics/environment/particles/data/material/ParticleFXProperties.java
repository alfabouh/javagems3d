package javagems3d.graphics.environment.particles.data.material;

public final class ParticleFXProperties {
    private float emissionStrength;
    private float alphaDiscard;

    public ParticleFXProperties(float emissionStrength, float alphaDiscard) {
        this.emissionStrength = emissionStrength;
        this.alphaDiscard = alphaDiscard;
    }

    public float getEmissionStrength() {
        return this.emissionStrength;
    }

    public ParticleFXProperties setEmissionStrength(float emissionStrength) {
        this.emissionStrength = emissionStrength;
        return this;
    }

    public float getAlphaDiscard() {
        return this.alphaDiscard;
    }

    public ParticleFXProperties setAlphaDiscard(float alphaDiscard) {
        this.alphaDiscard = alphaDiscard;
        return this;
    }
}
