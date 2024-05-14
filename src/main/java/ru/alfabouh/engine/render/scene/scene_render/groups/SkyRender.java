package ru.alfabouh.engine.render.scene.scene_render.groups;

import org.joml.Matrix4d;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.engine.game.resources.ResourceManager;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.game.resources.assets.models.mesh.Mesh;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.render.environment.sky.Sky;
import ru.alfabouh.engine.render.scene.Scene;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.SceneRenderBase;
import ru.alfabouh.engine.render.scene.scene_render.RenderGroup;
import ru.alfabouh.engine.render.transformation.TransformationManager;

public class SkyRender extends SceneRenderBase {
    private static final float[] skyboxPos = {
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, -1.0f,
            1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f
    };
    private static final int[] skyboxInd = new int[]{
            0, 1, 3, 3, 1, 2,
            4, 0, 3, 5, 4, 3,
            3, 2, 7, 5, 3, 7,
            6, 1, 0, 6, 0, 4,
            2, 1, 6, 2, 6, 7,
            7, 6, 4, 7, 4, 5
    };

    public static Model<Format3D> skyBoxModel;

    public SkyRender(SceneRender sceneRenderConveyor) {
        super(12, sceneRenderConveyor, new RenderGroup("SKYBOX"));
        Mesh mesh = new Mesh();
        mesh.putPositionValues(SkyRender.skyboxPos);
        mesh.putIndexValues(SkyRender.skyboxInd);
        mesh.bakeMesh();
        SkyRender.skyBoxModel = new Model<>(new Format3D(), mesh);
    }

    private void renderCubeMapSkyBox() {
       Sky sky = this.getSceneWorld().getEnvironment().getSky();
       ShaderManager shaderManager = ResourceManager.shaderAssets.skybox;
       Model<Format3D> model = SkyRender.skyBoxModel;
       shaderManager.bind();
       GL30.glDisable(GL30.GL_CULL_FACE);
       GL30.glDepthFunc(GL30.GL_LEQUAL);
       shaderManager.getUtils().performProjectionMatrix();
       Matrix4d matrix4d = TransformationManager.instance.getModelViewMatrix(model);
       matrix4d.m30(0);
       matrix4d.m31(0);
       matrix4d.m32(0);
       shaderManager.performUniform("covered_by_fog", sky.isCoveredByFog() ? 1 : 0);
       shaderManager.getUtils().performModelViewMatrix3d(matrix4d);
       shaderManager.getUtils().setCubeMapTexture(sky.getSkyBox().cubeMapTexture());
       //shaderManager.getUtils().setCubeMapTexture(Game.getGame().getScreen().getScene().getSceneRender().getShadowScene().getPointLightShadows().get(0).getPointLightCubeMap().getCubeMapProgram());
       Scene.renderModel(model, GL30.GL_TRIANGLES);
       shaderManager.unBind();
       GL30.glDepthFunc(GL30.GL_LESS);
       GL30.glEnable(GL30.GL_CULL_FACE);
    }

    public void onRender(double partialTicks) {
        this.renderCubeMapSkyBox();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}