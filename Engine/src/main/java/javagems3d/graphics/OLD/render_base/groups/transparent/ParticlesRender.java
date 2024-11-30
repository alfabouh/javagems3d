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

package javagems3d.graphics.OLD.render_base.groups.transparent;

import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.particles.objects.base.ParticleFX;
import javagems3d.graphics.rendering.JGemsSceneUtils;
import javagems3d.graphics.OLD.JGemsOpenGLRendererOLD;
import javagems3d.graphics.OLD.render_base.RenderGroup;
import javagems3d.graphics.OLD.render_base.SceneRenderBase;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public class ParticlesRender extends SceneRenderBase {
    public ParticlesRender(JGemsOpenGLRendererOLD sceneRender) {
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
        gemsShaderManager.beginShading();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(model);
        gemsShaderManager.getUtils().performShadowsInfo();
        if (particleFX.hasTexturePack()) {
            gemsShaderManager.performUniformTexture(new UniformString("diffuse_map"), particleFX.getCurrentFrame().getTextureId(), GL46.GL_TEXTURE_2D);
            gemsShaderManager.performUniform(new UniformString("use_texture"), UniformFunctions.BOOLEAN(true));
        } else {
            gemsShaderManager.performUniform(new UniformString("use_texture"), UniformFunctions.BOOLEAN(false));
        }
        gemsShaderManager.performUniform(new UniformString("color_mask"), UniformFunctions.VEC3F(particleFX.getColorMask()));
        gemsShaderManager.performUniform(new UniformString("brightness"), UniformFunctions.FLOAT(particleFX.getParticleAttributes().getBrightness()));
        gemsShaderManager.performUniform(new UniformString("alpha_factor"), UniformFunctions.FLOAT(particleFX.getParticleAttributes().getOpacity()));
        JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
        gemsShaderManager.endShading();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}