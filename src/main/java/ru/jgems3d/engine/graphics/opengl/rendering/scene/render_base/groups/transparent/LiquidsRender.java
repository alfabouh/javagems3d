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

package ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.groups.transparent;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.graphics.opengl.frustum.ICulled;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsSceneUtils;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.LiquidObject;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;
import ru.jgems3d.engine.system.resources.assets.shaders.RenderPass;
import ru.jgems3d.engine.system.resources.assets.shaders.UniformString;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class LiquidsRender extends SceneRenderBase {
    public LiquidsRender(JGemsOpenGLRenderer sceneRender) {
        super(2, sceneRender, new RenderGroup("LIQUIDS_TRANSPARENT"));
    }

    public void onRender(FrameTicking frameTicking) {
        for (ICulled culled : this.getSceneWorld().getCollectionFrustumCulledList(this.getSceneWorld().getLiquids())) {
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
        gemsShaderManager.bind();
        gemsShaderManager.getUtils().performPerspectiveMatrix();
        gemsShaderManager.getUtils().performViewAndModelMatricesSeparately(object.getModel());
        for (ModelNode modelNode : object.getModel().getMeshDataGroup().getModelNodeList()) {
            gemsShaderManager.getUtils().performShadowsInfo();
            gemsShaderManager.getUtils().performModelMaterialOnShader(object.getRenderLiquidData().getLiquidMaterial());
            gemsShaderManager.performUniform(new UniformString("alpha_factor"), object.getRenderLiquidData().getLiquidMaterial().getFullOpacity());
            gemsShaderManager.performUniform(new UniformString("texture_scaling"), object.getTextureScaling());
            JGemsSceneUtils.renderModelNode(modelNode);
            gemsShaderManager.updateTextureUnitSlots();
        }
        gemsShaderManager.unBind();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}