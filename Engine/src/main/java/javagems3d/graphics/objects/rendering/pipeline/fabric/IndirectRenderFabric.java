package javagems3d.graphics.objects.rendering.pipeline.fabric;

import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import javagems3d.graphics.rendering.programs.indirect.IndirectBufferCommandsBuilder;
import javagems3d.graphics.rendering.programs.indirect.IndirectRenderBufferProgram;
import javagems3d.graphics.rendering.programs.shaders.unifrom.UniformFunctions;
import javagems3d.graphics.rendering.scene.renderer.indirect.IndirectObjectsRenderer;
import javagems3d.graphics.transformation.JGemsTransformation;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.assets.shaders.uniform.UniformString;
import javagems3d.system.resources.assets.texturing.CubeMapTexture;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.function.Consumer;

public abstract class IndirectRenderFabric implements IRenderFabric {
    public static final IndirectObjectsRenderer.RenderingFunction defaultFuncScene = new DefaultIndirectFunctionForScene();
    public static final IndirectObjectsRenderer.RenderingFunction defaultFuncShadow = new DefaultIndirectFunctionForShadow();

    private final IndirectObjectsRenderer.RenderingFunction renderingFunction;
    private final Stage stage;

    public IndirectRenderFabric(@NotNull Stage stage, @NotNull IndirectObjectsRenderer.RenderingFunction renderingFunction) {
        this.renderingFunction = renderingFunction;
        if (!stage.getType().equals(Type.INDIRECT)) {
            JGemsHelper.getLogger().warn("RenderFabric type doesn't belong to INDIRECT");
        }
        this.stage = stage;
    }

    public abstract void onFillBufferWithMatrices(Pipeline pipeline, IRendered renderedItem, Matrix4f defaultMatrix, FloatBuffer matrices, ArbitraryArguments metaData);
    public abstract void onFillBufferWithProperties(Pipeline pipeline, IRendered renderedItem, RenderAttributes defaultAttributes, ByteBuffer properties, ArbitraryArguments metaData);

    public @NotNull Stage getRenderingStage() {
        return this.stage;
    }

    public @NotNull IndirectObjectsRenderer.RenderingFunction getRenderingFunction() {
        return this.renderingFunction;
    }

    public static class DefaultIndirectFunctionForScene implements IndirectObjectsRenderer.RenderingFunction {
        @Override
        public void func(JGemsShaderManager shaderManager, IndirectBufferCommandsBuilder indirectBufferCommandsBuilder, IndirectRenderBufferProgram renderBuffer, @NotNull ArbitraryArguments metaData) {
            shaderManager.beginShading();
            CubeMapTexture cubeMapProgram = JGemsHelper.ENVIRONMENT.getWorldEnvironment().getSkyBox().getSky2DTexture();
            shaderManager.performUniformNoWarn(new UniformString("camera_pos"), UniformFunctions.VEC3F(JGemsHelper.CAMERA.getCurrentCamera().getCamPosition()));
            if (cubeMapProgram != null && shaderManager.isUniformExist(new UniformString("ambient_cube_map"))) {
                shaderManager.performUniformTexture(new UniformString("ambient_cube_map"), cubeMapProgram);
            }
            shaderManager.performUniform(new UniformString("projection_matrix"), UniformFunctions.MAT4F(JGemsTransformation.INSTANCE.getPerspectiveMatrix()));
            shaderManager.performUniform(new UniformString("view_matrix"), UniformFunctions.MAT4F(JGemsTransformation.INSTANCE.getCameraViewMatrix()));
            GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, indirectBufferCommandsBuilder.getRenderBufferHandle());
            GL46.glBindVertexArray(renderBuffer.getStaticVao());
            GL46.glMultiDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, 0, indirectBufferCommandsBuilder.getDrawCount(), 0);
            GL46.glBindVertexArray(0);
            GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);
            shaderManager.endShading();
        }

        @Override
        public int uniqueFunctionID() {
            return 0;
        }
    }

    public static class DefaultIndirectFunctionForShadow implements IndirectObjectsRenderer.RenderingFunction {
        @Override
        public void func(JGemsShaderManager shaderManager, IndirectBufferCommandsBuilder indirectBufferCommandsBuilder, IndirectRenderBufferProgram renderBuffer, @NotNull ArbitraryArguments metaData) {
            Consumer<JGemsShaderManager> functionToHandleUniforms = metaData.getterFunc().getObject(0);
            shaderManager.beginShading();
            if (functionToHandleUniforms != null) {
                functionToHandleUniforms.accept(shaderManager);
            }
            GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, indirectBufferCommandsBuilder.getRenderBufferHandle());
            GL46.glBindVertexArray(renderBuffer.getStaticVao());
            GL46.glMultiDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, 0, indirectBufferCommandsBuilder.getDrawCount(), 0);
            GL46.glBindVertexArray(0);
            GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);
            shaderManager.endShading();
        }

        @Override
        public int uniqueFunctionID() {
            return 1;
        }
    }
}