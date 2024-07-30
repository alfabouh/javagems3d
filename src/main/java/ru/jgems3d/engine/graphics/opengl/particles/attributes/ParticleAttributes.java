package ru.jgems3d.engine.graphics.opengl.particles.attributes;

@SuppressWarnings("all")
public class ParticleAttributes {
    private float distanceToRender;
    private boolean isBright;

    public ParticleAttributes(float distanceToRender) {
        this.distanceToRender = distanceToRender;
        this.isBright = false;
    }

    public float getDistanceToRender() {
        return distanceToRender;
    }
}
