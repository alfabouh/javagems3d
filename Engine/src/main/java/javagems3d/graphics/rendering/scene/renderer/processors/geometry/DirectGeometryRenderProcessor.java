package javagems3d.graphics.rendering.scene.renderer.processors.geometry;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.DirectRenderFabric;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private Collection<SceneObject> sceneObjects;
    private final Pipeline pipeline;

    public DirectGeometryRenderProcessor(@NotNull Pipeline pipeline, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.pipeline = pipeline;
    }

    @Override
    public void createResources() {
    }

    @Override
    public void destroyResources() {
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        Pipeline pipeline = this.getPipeline();
        Map<JGemsShaderManager, List<SceneObject>> groupedObjects = this.getSceneObjects().stream().collect(Collectors.groupingBy(e -> e.getRenderingTable().getShaderManager(pipeline)));
        for (Map.Entry<JGemsShaderManager, List<SceneObject>> entry : groupedObjects.entrySet()) {
            JGemsShaderManager shaderManager = entry.getKey();
            shaderManager.beginShading();
            for (SceneObject modeledSceneObject : entry.getValue()) {
                Model<Format3D> model = modeledSceneObject.getModel();
                if (model == null || model.getMeshStructure() == null) {
                    continue;
                }
                DirectRenderFabric directRenderFabric = modeledSceneObject.getRenderingTable().getRenderFabric(pipeline);
                directRenderFabric.onPreRender(pipeline, shaderManager, this.getOpenGLRenderer(), modeledSceneObject, null);
                directRenderFabric.onRender(pipeline, shaderManager, this.getOpenGLRenderer(), modeledSceneObject, null);
                directRenderFabric.onPostRender(pipeline, shaderManager, this.getOpenGLRenderer(), modeledSceneObject, null);
            }
            shaderManager.endShading();
        }
    }

    public void setDirectMeshObjects(@NotNull Collection<SceneObject> sceneObjects) {
        this.sceneObjects = sceneObjects;
    }

    public Pipeline getPipeline() {
        return this.pipeline;
    }

    public Collection<SceneObject> getSceneObjects() {
        return this.sceneObjects;
    }
}