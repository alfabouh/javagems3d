package javagems3d.graphics.rendering.scene.renderer.processors.geometry;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Redirections;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.collections.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class DirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private Collection<SceneObject> sceneObjects;
    private final Pipeline pipeline;
    private final Set<SceneObject> rejected;
    private Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandler;

    public DirectGeometryRenderProcessor(@Nullable Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandler, @NotNull Pipeline pipeline, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.pipeline = pipeline;
        this.rejected = new HashSet<>();
        this.uniformsHandler = uniformsHandler;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
        this.getRejected().clear();
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.getRejected().clear();

        Pipeline pipeline = this.getPipeline();
        Map<JGemsShaderManager, List<SceneObject>> groupedObjects = OpenGLRenderer.groupObjectsByShaders(this.getSceneObjects(), pipeline);
        for (Map.Entry<JGemsShaderManager, List<SceneObject>> entry : groupedObjects.entrySet()) {
            JGemsShaderManager shaderManager = entry.getKey();
            shaderManager.beginShading();
            for (SceneObject sceneObject : entry.getValue()) {
                Model3D model = sceneObject.getModel();
                if (model == null || !model.isValid()) {
                    continue;
                }
                if (pipeline.equals(Pipeline.SOLID_SCENE) && !sceneObject.getRenderTable().isRedirected(Redirections.TRANSPARENCY__IN__SOLID_SCENE)) {
                    if (sceneObject.getModel().getMeshStructure().hasTransparency()) {
                        this.getRejected().add(sceneObject);
                    }
                }
                DirectRenderFabric directRenderFabric = Objects.requireNonNull(sceneObject.getRenderTable().getRenderingData(pipeline)).getRenderFabric();
                directRenderFabric.onPreRender(pipeline, shaderManager, this.getOpenGLRenderer(), sceneObject, null);
                directRenderFabric.onRender(pipeline, shaderManager, this.getOpenGLRenderer(), sceneObject, ArbitraryArguments.pass(this.getUniformsHandler()));
                directRenderFabric.onPostRender(pipeline, shaderManager, this.getOpenGLRenderer(), sceneObject, null);
            }
            shaderManager.endShading();
        }
    }

    public Set<SceneObject> getRejected() {
        return this.rejected;
    }

    public void setDirectMeshObjects(@NotNull Collection<SceneObject> sceneObjects) {
        this.sceneObjects = sceneObjects;
    }

    public void setUniformsHandler(Consumer<Pair<JGemsShaderManager, IRendered>> uniformsHandler) {
        this.uniformsHandler = uniformsHandler;
    }

    public Consumer<Pair<JGemsShaderManager, IRendered>> getUniformsHandler() {
        return this.uniformsHandler;
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public Collection<SceneObject> getSceneObjects() {
        return this.sceneObjects;
    }
}