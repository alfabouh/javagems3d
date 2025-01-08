package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.fbo.attachments.T2DAttachmentContainer;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;

import java.util.*;

public class IndirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private FBOTexture2DProgram gBuffer;
    private final IndirectObjectsRenderer indirectMeshObjects;

    public IndirectGeometryRenderProcessor(@NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectMeshObjects = new IndirectObjectsRenderer(openGLRenderer, null, null, true, true);
    }

    @Override
    public void createResources() {
        this.gBuffer = new FBOTexture2DProgram(true);
        T2DAttachmentContainer gBuffer = new T2DAttachmentContainer() {{
            add(GL46.GL_COLOR_ATTACHMENT0, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT1, GL46.GL_RGB32F, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT2, GL46.GL_RGBA, GL46.GL_RGBA);
            add(GL46.GL_COLOR_ATTACHMENT3, GL46.GL_RGB, GL46.GL_RGB);
            add(GL46.GL_COLOR_ATTACHMENT4, GL46.GL_RGB, GL46.GL_RGB);
        }};
        this.gBuffer.createFrameBuffer2DTexture(this.getOpenGLRenderer().getRenderingResolution(), gBuffer, true, GL46.GL_NEAREST, GL46.GL_COMPARE_REF_TO_TEXTURE, GL46.GL_LESS, GL46.GL_CLAMP_TO_EDGE, null);
        //JGems3D.get().getResourceManager().getGlobalResources().getResourceCache().clearGroupInCache(MeshBuffer.class);
        //JGemsResourceManager.globalModelAssets.load(JGems3D.get().getResourceManager().getGlobalResources());
        //((JGemsOpenGLRenderer) JGemsHelper.getScreen().getScene().getSceneRenderer()).initSceneIndirectRenderBuffer(JGems3D.get().getResourceManager().getResourceDataCache().getMeshBuffersDataCache()); //DEBUG
        //       ((JGemsOpenGLRenderer) JGemsHelper.getScreen().getScene().getSceneRenderer()).loadMeshMaterialsIsSSBO(JGems3D.get().getResourceManager().getResourceDataCache().getBindlessTexturesCache(), JGems3D.get().getResourceManager().getResourceDataCache().getMeshBuffersDataCache());
    }

    @Override
    public void destroyResources() {
        if (this.getGBuffer() != null) {
            this.getGBuffer().clearFBO();
        }
    }

    @Override
    public void onRender(FrameTicking frameTicking) {
        this.getGBuffer().bindFBO();
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
        this.getIndirectMeshObjects().processAndRender();
        this.getGBuffer().unBindFBO();
    }

    public IndirectObjectsRenderer getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }

    public void setIndirectMeshObjects(@NotNull Set<SceneObject> sceneObjects) {
        this.getIndirectMeshObjects().setIndirectMeshObjects(sceneObjects);
    }

    public FBOTexture2DProgram getGBuffer() {
        return this.gBuffer;
    }
}