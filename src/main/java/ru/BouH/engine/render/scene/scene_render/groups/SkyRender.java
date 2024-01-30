package ru.BouH.engine.render.scene.scene_render.groups;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL30;
import ru.BouH.engine.game.resources.ResourceManager;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.RenderManager;
import ru.BouH.engine.render.scene.Scene;
import ru.BouH.engine.render.scene.SceneRenderBase;
import ru.BouH.engine.render.scene.scene_render.RenderGroup;

public class SkyRender extends SceneRenderBase {

    public SkyRender(Scene.SceneRenderConveyor sceneRenderConveyor) {
        super(0, sceneRenderConveyor, new RenderGroup("SKYBOX", true));
    }

    public void onRender(double partialTicks) {
        ShaderManager shaderManager = ResourceManager.shaderAssets.skybox;
        Model<Format3D> model = this.getSceneWorld().getEnvironment().getSky().getModel3D();
        shaderManager.bind();
        GL30.glDisable(GL30.GL_CULL_FACE);
        GL30.glDepthFunc(GL30.GL_LEQUAL);
        shaderManager.getUtils().performProjectionMatrix();
        Matrix4d matrix4d = RenderManager.instance.getModelViewMatrix(model);
        matrix4d.m30(0);
        matrix4d.m31(0);
        matrix4d.m32(0);
        shaderManager.getUtils().performModelViewMatrix3d(matrix4d);
        shaderManager.getUtils().setCubeMapTexture(this.getSceneWorld().getEnvironment().getSky().getCubeMapProgram());
        Scene.renderModel(model, GL30.GL_TRIANGLES);
        shaderManager.unBind();
        GL30.glDepthFunc(GL30.GL_LESS);
        GL30.glEnable(GL30.GL_CULL_FACE);
    }

    @Override
    public void onStartRender() {
    }

    @Override
    public void onStopRender() {
    }
}
