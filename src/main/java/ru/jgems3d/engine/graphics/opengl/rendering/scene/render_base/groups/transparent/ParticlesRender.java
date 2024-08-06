package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.particles.objects.base.ParticleFX;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class ParticlesRender extends SceneRenderBase {
    public ParticlesRender(JGemsOpenGLRenderer sceneRender) {
        super(122, sceneRender, new RenderGroup("PARTICLE_TRANSPARENT"));
    }

    public void onRender(FrameTicking frameTicking) {
        for (ParticleFX particleFX : this.getSceneWorld().getParticlesEmitter().getCulledParticlesSet(this.getSceneRenderer().getSceneData())) {
            this.renderParticleSceneObject(particleFX);
        }
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