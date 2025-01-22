package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.skybox.SkyBox;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.predefined.IDeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

public class SkyboxRenderProcessor extends IRenderProcessor.Template {
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
    private Model<Format3D> skyBoxModel;

    private final IDeferredRenderNode deferredRenderNode;
    private final SkyBox skyBox;

    public SkyboxRenderProcessor(@NotNull SkyBox skyBox, @NotNull IDeferredRenderNode deferredRenderNode, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.skyBox = skyBox;
        this.deferredRenderNode = deferredRenderNode;
    }

    @Override
    public void createResources() {
        RenderMesh mesh = new RenderMesh();
        FloatVertexAttribute positions = new FloatVertexAttribute(DefaultAttributePointers.ATTR_POSITIONS);
        positions.putArray(SkyboxRenderProcessor.skyboxPos);
        mesh.addVertexAttributeInMesh(positions);
        mesh.putVertexIndexes(SkyboxRenderProcessor.skyboxInd);
        mesh.bakeMesh();
        this.skyBoxModel = new Model<>(new Format3D(), new MeshGroup(mesh));
    }

    @Override
    public void destroyResources() {
        if (this.skyBoxModel != null) {
            this.skyBoxModel.clear();
        }
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.renderSkyBox(JGemsOpenGLRenderer.SkyBoxShader());
    }

    protected void renderSkyBox(JGemsShaderManager skyShaderManager) {
        Model<Format3D> model = this.skyBoxModel;
        skyShaderManager.beginShading();
        GL46.glDisable(GL46.GL_CULL_FACE);
        GL46.glDepthFunc(GL46.GL_LEQUAL);
        skyShaderManager.getUtils().performPerspectiveMatrix();
        Matrix4f Matrix4f = JGemsTransformation.getModelViewMatrix(model);
        Matrix4f.m30(0);
        Matrix4f.m31(0);
        Matrix4f.m32(0);
        //skyShaderManager.performUniformTexture(new UniformString("skybox_background_sampler"), this.getSceneRenderer().getSkyBoxBackGroundBuffer().getTextureIDByIndex(0), GL46.GL_TEXTURE_2D);
        skyShaderManager.performUniform(new UniformString("covered_by_fog"), UniformFunctions.BOOLEAN(this.getSkyBox().isSkyCoveredByFog()));
        skyShaderManager.performUniform(new UniformString("view_mat_inverted"), UniformFunctions.MAT4F(JGemsTransformation.INSTANCE.getCameraViewMatrix().invert()));
        skyShaderManager.getUtils().performModel3DViewMatrix(Matrix4f);
        skyShaderManager.performUniformTexture(new UniformString("skybox"), this.getSkyBox().getSky2DTexture());
        JGemsHelper.RENDERING.renderModel(model, GL46.GL_TRIANGLES);
        skyShaderManager.endShading();
        GL46.glDepthFunc(GL46.GL_LESS);
        GL46.glEnable(GL46.GL_CULL_FACE);
    }

    public SkyBox getSkyBox() {
        return this.skyBox;
    }

    public IDeferredRenderNode getDeferredRenderNode() {
        return this.deferredRenderNode;
    }
}
