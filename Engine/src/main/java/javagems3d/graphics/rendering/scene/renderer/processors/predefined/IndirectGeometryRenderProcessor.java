package javagems3d.graphics.rendering.scene.renderer.processors.predefined;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class IndirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private final IndirectObjectsRenderer indirectMeshObjects;

    public IndirectGeometryRenderProcessor(@NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectMeshObjects = new IndirectObjectsRenderer(openGLRenderer, null, true, true);
    }

    @Override
    public void createResources() {
        //JGems3D.get().getResourceManager().getGlobalResources().getResourceCache().clearGroupInCache(MeshBuffer.class);
        //JGemsResourceManager.globalModelAssets.load(JGems3D.get().getResourceManager().getGlobalResources());
        //((JGemsOpenGLRenderer) JGemsHelper.getScreen().getScene().getSceneRenderer()).initSceneIndirectRenderBuffer(JGems3D.get().getResourceManager().getResourceDataCache().getMeshBuffersDataCache()); //DEBUG
        //       ((JGemsOpenGLRenderer) JGemsHelper.getScreen().getScene().getSceneRenderer()).loadMeshMaterialsIsSSBO(JGems3D.get().getResourceManager().getResourceDataCache().getBindlessTexturesCache(), JGems3D.get().getResourceManager().getResourceDataCache().getMeshBuffersDataCache());
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.getIndirectMeshObjects().processAndRender(Pipeline.SCENE, null);
    }

    public IndirectObjectsRenderer getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }

    public void setIndirectMeshObjects(@NotNull Collection<SceneObject> sceneObjects) {
        this.getIndirectMeshObjects().setIndirectMeshObjects(sceneObjects);
    }
}