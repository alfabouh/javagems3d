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

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.rendering.JGemsSceneUtils;
import javagems3d.graphics.objects.entities.LiquidObject;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.OLD.JGemsOpenGLRendererOLD;
import javagems3d.graphics.OLD.render_base.RenderGroup;
import javagems3d.graphics.OLD.render_base.SceneRenderBase;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;

public class LiquidsRender extends SceneRenderBase {
    public LiquidsRender(JGemsOpenGLRendererOLD sceneRender) {
        super(2, sceneRender, new RenderGroup("LIQUIDS_TRANSPARENT"));
    }

    public void onRender(FrameTicking frameTicking) {
        for (ICulled culled : this.getSceneWorld().getLiquids()) {
            LiquidObject liquidObject = (LiquidObject) culled;
            this.renderLiquid(liquidObject);
        }
    }

    private void renderLiquid(LiquidObject object) {
        JGemsShaderManager gemsShaderManager = object.getRenderLiquidData().getShaderManager();
        if (!gemsShaderManager.getShaderRenderPass().equals(RenderPass.TRANSPARENCY)) {
            JGemsHelper.getLogger().warn("Particle should have transparency shader!");
            return;
        }
        gemsShaderManager.beginShading();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(object.getModel());
        for (MeshGroup.MeshGroupNode meshNode : object.getModel().<MeshGroup>getMeshStructureWithUnSafeCast().getMeshNodes()) {
            gemsShaderManager.getUtils().performShadowsInfo();
            gemsShaderManager.getUtils().performModelMaterialOnShader(object.getRenderLiquidData().getLiquidMaterial());
            gemsShaderManager.performUniform(new UniformString("alpha_factor"), UniformFunctions.FLOAT(object.getRenderLiquidData().getLiquidMaterial().getFullOpacity()));
            gemsShaderManager.performUniform(new UniformString("texture_scaling"), UniformFunctions.VEC2F(object.getTextureScaling()));
            JGemsSceneUtils.renderModelNode(meshNode);
            gemsShaderManager.clearUsedTextureSlots();
        }
        gemsShaderManager.endShading();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}