package ru.alfabouh.jgems3d.engine.render.opengl.scene.components.groups;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;
import ru.alfabouh.jgems3d.engine.render.opengl.environment.sky.Sky;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.utils.JGemsSceneUtils;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.JGemsSceneRender;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.base.SceneRenderBase;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.components.RenderGroup;
import ru.alfabouh.jgems3d.engine.render.transformation.Transformation;
import ru.alfabouh.jgems3d.engine.system.resources.ResourceManager;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.mesh.Mesh;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

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

    public SkyRender(JGemsSceneRender sceneRenderConveyor) {
        super(12, sceneRenderConveyor, new RenderGroup("SKYBOX"));
        Mesh mesh = new Mesh();
        mesh.putPositionValues(SkyRender.skyboxPos);
        mesh.putIndexValues(SkyRender.skyboxInd);
        mesh.bakeMesh();
        SkyRender.skyBoxModel = new Model<>(new Format3D(), mesh);
    }

    private void renderCubeMapSkyBox() {
       Sky sky = this.getSceneWorld().getEnvironment().getSky();
       JGemsShaderManager shaderManager = ResourceManager.shaderAssets.skybox;
       Model<Format3D> model = SkyRender.skyBoxModel;
       shaderManager.bind();
       GL30.glDisable(GL30.GL_CULL_FACE);
       GL30.glDepthFunc(GL30.GL_LEQUAL);
       shaderManager.getUtils().performPerspectiveMatrix();
       Matrix4f Matrix4f = Transformation.getModelViewMatrix(model.getFormat(), JGemsSceneUtils.getMainCameraViewMatrix());
       Matrix4f.m30(0);
       Matrix4f.m31(0);
       Matrix4f.m32(0);
       shaderManager.performUniform("covered_by_fog", sky.isCoveredByFog());
       shaderManager.getUtils().performModel3DViewMatrix(Matrix4f);
       shaderManager.getUtils().performCubeMap(sky.getSkyBox().cubeMapTexture());
       //shaderManager.getUtils().setCubeMapTexture(JGems.getGame().getScreen().getScene().getSceneRender().getShadowScene().getPointLightShadows().get(0).getPointLightCubeMap().getCubeMapProgram());
       JGemsSceneUtils.renderModel(model, GL30.GL_TRIANGLES);
       shaderManager.unBind();
       GL30.glDepthFunc(GL30.GL_LESS);
       GL30.glEnable(GL30.GL_CULL_FACE);
    }

    public void onRender(float partialTicks) {
        this.renderCubeMapSkyBox();
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}