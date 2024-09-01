/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent;

import org.lwjgl.opengl.GL30;
import javagems3d.engine.graphics.opengl.particles.objects.base.ParticleFX;
import javagems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.engine.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.engine.system.resources.assets.models.Model;
import javagems3d.engine.system.resources.assets.models.formats.Format3D;
import javagems3d.engine.system.resources.assets.shaders.UniformString;
import javagems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class ParticlesRender extends SceneRenderBase {
    public ParticlesRender(JGemsOpenGLRenderer sceneRender) {
        super(3, sceneRender, new RenderGroup("PARTICLE_TRANSPARENT"));
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
            gemsShaderManager.performUniformTexture(new UniformString("diffuse_map"), particleFX.getCurrentFrame().getTextureId(), GL30.GL_TEXTURE_2D);
            gemsShaderManager.performUniform(new UniformString("use_texture"), true);
        } else {
            gemsShaderManager.performUniform(new UniformString("use_texture"), false);
        }
        gemsShaderManager.performUniform(new UniformString("color_mask"), particleFX.getColorMask());
        gemsShaderManager.performUniform(new UniformString("brightness"), particleFX.getParticleAttributes().getBrightness());
        gemsShaderManager.performUniform(new UniformString("alpha_factor"), particleFX.getParticleAttributes().getOpacity());
        JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
        gemsShaderManager.unBind();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}