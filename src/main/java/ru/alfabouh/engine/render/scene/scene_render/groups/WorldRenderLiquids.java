package ru.alfabouh.engine.render.scene.scene_render.groups;

import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.render.frustum.ICullable;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.objects.items.LiquidObject;
import ru.alfabouh.engine.render.scene.scene_render.RenderGroup;

public class WorldRenderLiquids extends SceneRenderBase {
    public WorldRenderLiquids(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(10, sceneRenderConveyor, new RenderGroup("LIQUIDS"));
    }

    public void onRender(double partialTicks) {
        for (ICullable cullable : this.getSceneWorld().filterCulled(this.getSceneWorld().getLiquids())) {
            LiquidObject liquidObject = (LiquidObject) cullable;
            Model<Format3D> model = liquidObject.getModel();
            ShaderManager shaderManager = liquidObject.getRenderLiquidData().getShaderManager();

            shaderManager.bind();
            shaderManager.getUtils().performProjectionMatrix();
            shaderManager.getUtils().passViewAndModelMatrices(model);
            shaderManager.getUtils().passShadowsInfo();
            shaderManager.getUtils().passCommonInfo();
            shaderManager.performUniform("texture_scaling", liquidObject.getTextureScaling());
            shaderManager.getUtils().passCubeMap("ambient_cubemap", liquidObject.getRenderLiquidData().getAmbient());
            shaderManager.performUniform("use_cubemap", liquidObject.getRenderLiquidData().reflections() ? 1 : 0);

            Scene.activeGlTexture(0);
            liquidObject.getRenderLiquidData().getLiquidTexture().bindTexture();
            shaderManager.performUniform("diffuse_map", 0);

            if (liquidObject.getRenderLiquidData().getLiquidNormals() != null) {
                Scene.activeGlTexture(1);
                liquidObject.getRenderLiquidData().getLiquidNormals().bindTexture();
                shaderManager.performUniform("normals_map", 1);
                shaderManager.performUniform("use_normals", 1);
            } else {
                shaderManager.performUniform("use_normals", 0);
            }

            Scene.renderModel(model, GL30.GL_TRIANGLES);
            shaderManager.unBind();
        }
    }

    public void onStartRender() {
    }

    public void onStopRender() {
    }
}