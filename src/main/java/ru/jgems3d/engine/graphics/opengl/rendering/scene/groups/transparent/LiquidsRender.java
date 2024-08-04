package ru.jgems3d.engine.graphics.opengl.rendering.scene.groups.transparent;

import org.lwjgl.opengl.GL30;
import ru.jgems3d.engine.graphics.opengl.frustum.ICulled;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.JGemsScene;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.RenderGroup;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.LiquidObject;
import ru.jgems3d.engine.graphics.opengl.rendering.utils.JGemsSceneUtils;
import ru.jgems3d.engine.system.resources.assets.models.Model;
import ru.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class LiquidsRender extends SceneRenderBase {
    public LiquidsRender(JGemsOpenGLRenderer sceneRender) {
        super(2, sceneRender, new RenderGroup("LIQUIDS_TRANSPARENT"));
    }

    public void onRender(float partialTicks) {
        for (ICulled culled : this.getSceneWorld().getCollectionFrustumCulledList(this.getSceneWorld().getLiquids())) {
            LiquidObject liquidObject = (LiquidObject) culled;
            Model<Format3D> model = liquidObject.getModel();
            JGemsShaderManager shaderManager = liquidObject.getRenderLiquidData().getShaderManager();

            shaderManager.bind();
            shaderManager.getUtils().performPerspectiveMatrix();
            shaderManager.getUtils().performViewAndModelMatricesSeparately(model);
            shaderManager.getUtils().performShadowsInfo();
            shaderManager.getUtils().performCameraData();
            shaderManager.performUniform("texture_scaling", liquidObject.getTextureScaling());
            shaderManager.performUniform("use_cubemap", liquidObject.getRenderLiquidData().reflections());

            JGemsScene.activeGlTexture(0);
            liquidObject.getRenderLiquidData().getLiquidTexture().bindTexture();
            shaderManager.performUniform("diffuse_map", 0);

            if (liquidObject.getRenderLiquidData().getLiquidNormals() != null) {
                JGemsScene.activeGlTexture(1);
                liquidObject.getRenderLiquidData().getLiquidNormals().bindTexture();
                shaderManager.performUniform("normals_map", 1);
                shaderManager.performUniform("use_normals", 1);
            } else {
                shaderManager.performUniform("use_normals", 0);
            }

            JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
            shaderManager.unBind();
        }
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}