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

package javagems3d.graphics.opengl.rendering.scene.render_base.groups.forward;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.environment.skybox.SkyBox;
import javagems3d.graphics.opengl.rendering.programs.shaders.unifrom.DefaultUniformActions;
import javagems3d.system.resources.assets.models.mesh.DirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL46;
import javagems3d.graphics.opengl.rendering.JGemsSceneUtils;
import javagems3d.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import javagems3d.graphics.opengl.rendering.scene.render_base.RenderGroup;
import javagems3d.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import javagems3d.graphics.opengl.rendering.scene.tick.FrameTicking;
import javagems3d.graphics.transformation.Transformation;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.manager.JGemsResourceManager;

public class SkyBoxCubeMapRender extends SceneRenderBase {
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
    private final SkyBox skyBox;

    public SkyBoxCubeMapRender(SkyBox skyBox, JGemsOpenGLRenderer sceneRender) {
        super(3, sceneRender, new RenderGroup("SKY_FORWARD"));
        DirectRenderMesh directRenderMesh = new DirectRenderMesh();
        FloatVertexAttribute vaPositions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        vaPositions.putArray(SkyBoxCubeMapRender.skyboxPos);
        directRenderMesh.putVertexIndexes(SkyBoxCubeMapRender.skyboxInd);
        directRenderMesh.addVertexAttributeInMesh(vaPositions);
        directRenderMesh.bakeMesh();
        SkyBoxCubeMapRender.skyBoxModel = new Model<>(new Format3D(), directRenderMesh);
        this.skyBox = skyBox;
    }

    private void renderSkyBoxTexture() {
        JGemsShaderManager shaderManager = JGemsResourceManager.globalShaderAssets.skybox;
        Model<Format3D> model = SkyBoxCubeMapRender.skyBoxModel;
        shaderManager.beginShading();
        GL46.glDisable(GL46.GL_CULL_FACE);
        GL46.glDepthFunc(GL46.GL_LEQUAL);
        shaderManager.getUtils().performPerspectiveMatrix();
        Matrix4f Matrix4f = Transformation.getModelViewMatrix(model.getFormat(), JGemsSceneUtils.getMainCameraViewMatrix());
        Matrix4f.m30(0);
        Matrix4f.m31(0);
        Matrix4f.m32(0);
        shaderManager.performUniformTexture(new UniformString("skybox_background_sampler"), this.getSceneRenderer().getSkyBoxBackGroundBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        shaderManager.performUniform(new UniformString("covered_by_fog"), DefaultUniformActions.BOOLEAN(this.getSkyBox().isSkyCoveredByFog()));
        shaderManager.performUniform(new UniformString("view_mat_inverted"), DefaultUniformActions.MAT4F(new Matrix4f(JGemsSceneUtils.getMainCameraViewMatrix()).invert()));
        shaderManager.getUtils().performModel3DViewMatrix(Matrix4f);
        shaderManager.getUtils().performCubeMapProgram(new UniformString("skybox"), this.getSkyBox().getSky2DTexture().getTextureId());
        //shaderManager.getUtils().performCubeMapProgram(new UniformString("skybox"), this.getSceneRenderer().getShadowScene().getPointLightShadows().get(0).getPointLightCubeMap().getCubeMapProgram().getTextureId());
        JGemsSceneUtils.renderModel(model, GL46.GL_TRIANGLES);
        shaderManager.endShading();
        GL46.glDepthFunc(GL46.GL_LESS);
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    public void onRender(FrameTicking frameTicking) {
        Vector3f camPos = JGemsHelper.CAMERA.getCurrentCamera().getCamPosition();
        if (camPos.x < -JGems3D.MAP_MAX_SIZE || camPos.y < -JGems3D.MAP_MAX_SIZE || camPos.z < -JGems3D.MAP_MAX_SIZE || camPos.x > JGems3D.MAP_MAX_SIZE || camPos.y > JGems3D.MAP_MAX_SIZE || camPos.z > JGems3D.MAP_MAX_SIZE) {
            return;
        }
        this.renderSkyBoxTexture();
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    public void onStartRender() {
        super.onStartRender();
    }

    public void onStopRender() {
        super.onStopRender();
    }
}