package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups.transparent;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.particles.objects.base.ParticleFX;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class ParticlesRender extends SceneRenderBase {
    public ParticlesRender(JGemsOpenGLRenderer sceneRender) {
        super(122, sceneRender, new RenderGroup("PARTICLE_TRANSPARENT"));
    }

    public void onRender(float partialTicks) {
        GL30.glEnable(GL30.GL_BLEND);
        GL30.glDepthMask(false);
        for (ParticleFX particleFX : this.getSceneWorld().getParticlesEmitter().getCulledParticlesSet(this.getSceneRenderer().getSceneData())) {
            this.renderParticleSceneObject(particleFX);
        }
        GL30.glDepthMask(true);
        GL30.glDisable(GL30.GL_BLEND);
    }

    private void renderParticleSceneObject(ParticleFX particleFX) {
        JGemsShaderManager gemsShaderManager = particleFX.getParticleAttributes().getShaderManager();
        Model<Format3D> model = this.getSceneWorld().getParticlesEmitter().getParticleModel(particleFX);
        gemsShaderManager.bind();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(model);
        gemsShaderManager.getUtils().performShadowsInfo();
        if (particleFX.hasTexturePack()) {
            gemsShaderManager.performUniformTexture("diffuse_map", particleFX.getCurrentFrame().getTextureId(), GL30.GL_TEXTURE_2D);
            gemsShaderManager.performUniform("use_texture", true);
        } else {
            gemsShaderManager.performUniform("use_texture", false);
        }
        gemsShaderManager.performUniform("color_mask", particleFX.getColorMask());
        gemsShaderManager.performUniform("brightness", particleFX.getParticleAttributes().getBrightness());
        gemsShaderManager.performUniform("alpha_factor", particleFX.getParticleAttributes().getOpacity());
        JGemsSceneUtils.renderModel(model, GL30.GL_TEXTURE_2D);
        gemsShaderManager.unBind();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}