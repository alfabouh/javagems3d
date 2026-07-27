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

package javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IRenderFabric;
import javagems3d.graphics.objects.rendering.pipeline.fabric.IndirectRenderFabric;
import javagems3d.graphics.rendering.programs.indirect.commands.DefaultIndirectCommandsProgram;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.IndirectCommandsProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class GroupedSceneObjectsIndirectRenderer extends IndirectSceneObjectsRenderer {
    public GroupedSceneObjectsIndirectRenderer(@NotNull OpenGLRenderer openGLRenderer, @NotNull ShaderStorageBufferObject indirectSSBO, @NotNull ShaderStorageBufferObject propertiesSSBO, @NotNull Pipeline pipeline, boolean usePropertiesSSBO, boolean useMaterialsSSBO) {
        super(openGLRenderer, indirectSSBO, propertiesSSBO, pipeline, usePropertiesSSBO, useMaterialsSSBO);
    }

    public void processAndRender(@Nullable ArbitraryArguments metaData) {
        if (!this.getRejected().isEmpty()) {
            this.getRejected().clear();
        }
        if (this.getIndirectMeshObjects() == null) {
            return;
        }
        IndirectBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        Map<Operator, Set<SceneObject>> map = this.groupObjects(this.getIndirectMeshObjects(), this.getPipeline());
        for (Map.Entry<Operator, Set<SceneObject>> sceneObjects : map.entrySet()) {
            Operator operator = sceneObjects.getKey();
            IntBuffer indexes = MemoryUtil.memAllocInt(GroupedSceneObjectsIndirectRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
            IntBuffer materialIds = MemoryUtil.memAllocInt(GroupedSceneObjectsIndirectRenderer.SSBO_DATASETS_MATERIAL_IDS_SIZE);
            IndirectCommandsProgram indirectCommandsProgram = this.createCommands(this.getMode(), indexes, this.isUseMaterialsSSBO() ? materialIds : null, renderBuffer, sceneObjects.getValue());
            this.fillSSBOWithSceneObjectInformation(indexes, materialIds, sceneObjects.getValue());
            this.render(operator, indirectCommandsProgram, renderBuffer, metaData);
            indirectCommandsProgram.destroyBuffer();
        }
    }

    protected IndirectCommandsProgram createCommands(Mode mode, @NotNull IntBuffer indexes, @Nullable IntBuffer materialIds, IndirectBufferProgram renderBuffer, Collection<SceneObject> sceneObjects) {
        DefaultIndirectCommandsProgram baseIndirectCommandProgram1 = new DefaultIndirectCommandsProgram(renderBuffer);
        baseIndirectCommandProgram1.createBuffer();
        baseIndirectCommandProgram1.buildCommands(indexes, materialIds, sceneObjects, mode);
        return baseIndirectCommandProgram1;
    }

    protected Map<IndirectSceneObjectsRenderer.Operator, Set<SceneObject>> groupObjects(@NotNull Collection<SceneObject> sceneObjects, Pipeline pipeline) {
        return sceneObjects.stream().collect(Collectors.groupingBy(e -> {
            RenderTable.Data renderingData = e.getRenderTable().getRenderingData(pipeline);
            IRenderFabric renderFabric = Objects.requireNonNull(renderingData).getRenderFabric();
            if (!renderFabric.getRenderingType().equals(Type.INDIRECT)) {
                throw new JGemsRuntimeException("RenderFabric-type should be INDIRECT");
            }
            return new IndirectSceneObjectsRenderer.Operator(((IndirectRenderFabric) renderFabric).getRenderingFunction(), renderingData.getShaderManager());
        }, HashMap::new, Collectors.toSet()));
    }
}