package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.particles.objects.ParticleFX;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.system.resources.assets.materials.samples.base.IImageSample;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

import java.util.Set;

public class ParticlesRender extends SceneRenderBase {
    public ParticlesRender(JGemsOpenGLRenderer sceneRender) {
        super(3, sceneRender, new RenderGroup("WORLD_PARTICLES_DEFERRED"));
    }

    public void onRender(float partialTicks) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        //GL30.glDepthFunc(GL30.GL_NOTEQUAL);
        this.render(partialTicks, this.getSceneWorld().getParticlesEmitter().getCulledParticlesSet(this.getSceneRenderer().getSceneData()));
        //GL30.glDepthFunc(GL30.GL_LESS);
        GL30.glDisable(GL30.GL_BLEND);
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
            Model<Format3D> model = this.getSceneWorld().getParticlesEmitter().getParticleModel(particleFX);
            particleFX.getParticleAttributes().getShaderManager().getUtils().performViewAndModelMatricesSeparately(model);
            particleFX.getParticleAttributes().getShaderManager().getUtils().performPerspectiveMatrix();
            JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
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