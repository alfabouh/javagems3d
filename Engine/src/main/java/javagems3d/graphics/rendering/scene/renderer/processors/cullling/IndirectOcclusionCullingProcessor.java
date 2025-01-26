package javagems3d.graphics.rendering.scene.renderer.processors.cullling;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Collection;
import java.util.function.Consumer;

public class IndirectOcclusionCullingProcessor extends IRenderProcessor.Template {
    private final IndirectObjectsRenderer indirectMeshObjects;
    public final Pipeline pipeline;
    private final Consumer<JGemsShaderManager> uniformsHandler;

    public IndirectOcclusionCullingProcessor(@NotNull Pipeline pipeline, @NotNull OpenGLRenderer openGLRenderer, @NotNull JGemsShaderManager cullingShader) {
        super(openGLRenderer);
        this.indirectMeshObjects = new IndirectObjectsRenderer(openGLRenderer, new IndirectObjectsRenderer.Operator(IndirectRenderFabric.DEFAULT_FUNC, cullingShader), false, false);
        this.pipeline = pipeline;
        this.uniformsHandler = (shaderManager) -> {
            final Matrix4f cameraMatrix = JGemsTransformation.INSTANCE.getCameraViewMatrix();
            final Matrix4f projection = JGemsTransformation.INSTANCE.getPerspectiveMatrix();
            shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(projection));
            shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(cameraMatrix));
        };
    }

    @Override
    public void createResources() {
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