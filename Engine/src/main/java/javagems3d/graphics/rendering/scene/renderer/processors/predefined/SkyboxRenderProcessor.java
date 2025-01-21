package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.predefined.IDeferredRenderNode;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.FloatVertexAttribute;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
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

    private FBOTexture2DProgram skybox;
    private final IDeferredRenderNode deferredRenderNode;

    public SkyboxRenderProcessor(@NotNull IDeferredRenderNode deferredRenderNode, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.deferredRenderNode = deferredRenderNode;
    }

    @Override
    public void createResources() {
        this.skybox = new FBOTexture2DProgram(true);
        T2DAttachmentContainer skybox = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB16F, GL46.GL_RGB);
        }};
        this.skybox.createFrameBuffer2DTexture(this.getRenderingResolution(), skybox, false, GL46.GL_NEAREST, GL46.GL_NONE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        
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
        if (this.getSkyBoxColorFBO() != null) {
            this.getSkyBoxColorFBO().clearFBO();
        }
        
        if (this.skyBoxModel != null) {
            this.skyBoxModel.clear();
        }
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.getSkyBoxColorFBO().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

        this.getSkyBoxColorFBO().unBindFBO();
    }

    protected void renderSkyBox() {

    }

    public IDeferredRenderNode getDeferredRenderNode() {
        return this.deferredRenderNode;
    }

    public FBOTexture2DProgram getSkyBoxColorFBO() {
        return this.skybox;
    }
}
