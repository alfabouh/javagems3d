package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.particles.objects.ParticleFX;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsScene;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneData;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.Set;

public class ParticlesRender extends SceneRenderBase {
    public ParticlesRender(SceneData sceneData) {
        super(3, sceneData, new RenderGroup("WORLD_PARTICLES_DEFERRED"));
    }

    public void onRender(float partialTicks) {
        this.render(partialTicks, this.getSceneData().getSceneWorld().getParticlesEmitter().getCulledParticlesSet(this.getSceneData()));
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }

    private void render(float partialTicks, Set<ParticleFX> renderObjects) {
        for (ParticleFX particleFX : renderObjects) {
            particleFX.getParticleAttributes().getShaderManager().bind();
            this.passDataInDeferredShader(particleFX.getParticleAttributes().getShaderManager(), particleFX);
            JGemsSceneUtils.renderModel(this.getSceneData().getSceneWorld().getParticlesEmitter().getParticleModel(particleFX), GL30.GL_TRIANGLES);
            particleFX.getParticleAttributes().getShaderManager().unBind();
        }
    }

    private void passDataInDeferredShader(JGemsShaderManager shaderManager, ParticleFX particleFX) {
        IImageSample imageSample = particleFX.getCurrentFrame();
        GL30.glActiveTexture(GL30.GL_TEXTURE0);
        imageSample.bindTexture();
        shaderManager.performUniform("diffuse_map", 0);

        shaderManager.performUniform("texture_scaling", new Vector2f(1.0f));
        shaderManager.performUniform("brightness", particleFX.getParticleAttributes().getBrightness());
    }
}