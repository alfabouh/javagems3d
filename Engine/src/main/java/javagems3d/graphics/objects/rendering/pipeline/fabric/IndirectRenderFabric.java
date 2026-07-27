/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.objects.rendering.pipeline.fabric;

import javagems3d.graphics.objects.IRendered;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.IndirectCommandsProgram;
import javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects.IndirectSceneObjectsRenderer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.function.Consumer;

public abstract class IndirectRenderFabric implements IRenderFabric {
    public static final IndirectSceneObjectsRenderer.IRenderingFunction DEFAULT_FUNC = new DefaultIndirectFunction();

    private final IndirectSceneObjectsRenderer.IRenderingFunction renderingFunction;
    private final Stage stage;

    public IndirectRenderFabric(@NotNull Stage stage, @NotNull IndirectSceneObjectsRenderer.IRenderingFunction renderingFunction) {
        this.renderingFunction = renderingFunction;
        if (!stage.getType().equals(Type.INDIRECT)) {
            Log.get().warn("RenderFabric type doesn't belong to INDIRECT");
        }
        this.stage = stage;
    }

    //public void preRender(Pipeline pipeline, IRendered renderedItem) {
    //}

    //public void postRender(Pipeline pipeline, IRendered renderedItem) {
    //}

    public abstract void onFillBufferWithMatrices(Pipeline pipeline, IRendered renderedItem, Matrix4f defaultMatrix, FloatBuffer matrices, ArbitraryArguments metaData);
    public abstract void onFillBufferWithProperties(Pipeline pipeline, IRendered renderedItem, RenderAttributes defaultAttributes, ByteBuffer properties, ArbitraryArguments metaData);

    public @NotNull Stage getRenderingStage() {
        return this.stage;
    }

    public @NotNull IndirectSceneObjectsRenderer.IRenderingFunction getRenderingFunction() {
        return this.renderingFunction;
    }

    public static class DefaultIndirectFunction implements IndirectSceneObjectsRenderer.IRenderingFunction {
        @Override
        public void func(JGemsShaderManager shaderManager, IndirectCommandsProgram indirectCommandsProgram, IndirectBufferProgram renderBuffer, @NotNull ArbitraryArguments metaData) {
            Consumer<JGemsShaderManager> functionToHandleUniforms = metaData.getterFunc().getObject(0);
            shaderManager.beginShading();
            if (functionToHandleUniforms != null) {
                functionToHandleUniforms.accept(shaderManager);
            }
            GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, indirectCommandsProgram.getRenderBufferHandle());
            GL46.glBindVertexArray(renderBuffer.getStaticVao());
            GL46.glMultiDrawElementsIndirect(GL46.GL_TRIANGLES, GL46.GL_UNSIGNED_INT, 0, indirectCommandsProgram.getDrawCount(), 0);
            GL46.glBindVertexArray(0);
            GL46.glBindBuffer(GL46.GL_DRAW_INDIRECT_BUFFER, 0);
            shaderManager.endShading();
        }

        @Override
        public int uniqueFunctionID() {
            return 0;
        }
    }
}