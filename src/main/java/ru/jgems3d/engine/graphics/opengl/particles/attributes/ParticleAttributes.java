package ru.jgems3d.engine.graphics.opengl.particles.attributes;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.jgems3d.engine.system.resources.manager.JGemsResourceManager;

@SuppressWarnings("all")
public class ParticleAttributes {
    private final JGemsShaderManager gBufferParticleShader;
    private float distanceToRender;
    private float brightness;
    private float opacity;

    public ParticleAttributes(JGemsShaderManager gBufferParticleShader, float distanceToRender) {
        this.gBufferParticleShader = gBufferParticleShader;
        this.distanceToRender = distanceToRender;
        this.brightness = 0.0f;
        this.opacity = 1.0f;
    }

    public ParticleAttributes setOpacity(float opacity) {
        this.opacity = opacity;
        return this;
    }

    public static ParticleAttributes defaultParticleAttributes() {
        return new ParticleAttributes(JGemsResourceManager.globalShaderAssets.world_particle, 128.0f);
    }

    public ParticleAttributes setBrightness(float brightness) {
        this.brightness = JGemsHelper.clamp(brightness, 0.0f, 1.0f);
        return this;
    }

    public ParticleAttributes setDistanceToRender(float distanceToRender) {
        this.distanceToRender = distanceToRender;
        return this;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public JGemsShaderManager getShaderManager() {
        return this.gBufferParticleShader;
    }

    public float getBrightness() {
        return this.brightness;
    }

    public float getDistanceToRender() {
        return this.distanceToRender;
    }
}
