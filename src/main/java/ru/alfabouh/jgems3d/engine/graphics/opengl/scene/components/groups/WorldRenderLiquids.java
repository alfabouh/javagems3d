package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.groups;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.graphics.opengl.frustum.ICullable;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsScene;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.JGemsOpenGLRenderer;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.items.LiquidObject;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public class WorldRenderLiquids extends SceneRenderBase {
    public WorldRenderLiquids(JGemsOpenGLRenderer sceneRenderConveyor) {
        super(10, sceneRenderConveyor, new RenderGroup("LIQUIDS_DEFERRED"));
    }

    public void onRender(float partialTicks) {
        for (ICullable cullable : this.getSceneWorld().getEntitiesFrustumCulledList(this.getSceneWorld().getLiquids())) {
            LiquidObject liquidObject = (LiquidObject) cullable;
            Model<Format3D> model = liquidObject.getModel();
            JGemsShaderManager shaderManager = liquidObject.getRenderLiquidData().getShaderManager();

            shaderManager.bind();
            shaderManager.getUtils().performPerspectiveMatrix();
            shaderManager.getUtils().performViewAndModelMatricesSeparately(model);
            shaderManager.getUtils().performShadowsInfo();
            shaderManager.getUtils().performCameraData();
            shaderManager.performUniform("texture_scaling", liquidObject.getTextureScaling());
            shaderManager.getUtils().performCubeMapProgram("ambient_cubemap", liquidObject.getRenderLiquidData().getAmbient());
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