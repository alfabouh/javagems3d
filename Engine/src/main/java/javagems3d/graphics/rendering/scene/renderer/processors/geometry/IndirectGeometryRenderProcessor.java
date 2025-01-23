package javagems3d.graphics.rendering.scene.renderer.processors.geometry;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public class IndirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private final IndirectObjectsRenderer indirectMeshObjects;
    public final Pipeline pipeline;
    private Consumer<JGemsShaderManager> uniformsHandler;

    public IndirectGeometryRenderProcessor(@Nullable Consumer<JGemsShaderManager> uniformsHandler, @NotNull Pipeline pipeline, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectMeshObjects = new IndirectObjectsRenderer(openGLRenderer, null, true, true);
        this.pipeline = pipeline;
        this.uniformsHandler = uniformsHandler;
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
        this.getIndirectMeshObjects().processAndRender(this.getPipeline(), ArbitraryArguments.pass(this.getUniformsHandler()));
    }

    public void setIndirectMeshObjects(@NotNull Collection<SceneObject> sceneObjects) {
        this.getIndirectMeshObjects().setIndirectMeshObjects(sceneObjects);
    }

    public void setUniformsHandler(Consumer<JGemsShaderManager> uniformsHandler) {
        this.uniformsHandler = uniformsHandler;
    }

    public Consumer<JGemsShaderManager> getUniformsHandler() {
        return this.uniformsHandler;
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public IndirectObjectsRenderer getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }
}