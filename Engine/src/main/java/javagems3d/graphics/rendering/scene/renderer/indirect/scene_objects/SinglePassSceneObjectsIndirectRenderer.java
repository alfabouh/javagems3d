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
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.programs.indirect.base.IndirectBufferProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.DefaultIndirectCommandsProgram;
import javagems3d.graphics.rendering.programs.indirect.commands.IndirectCommandsProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Collection;

public class SinglePassSceneObjectsIndirectRenderer extends IndirectSceneObjectsRenderer {
    private final Operator operator;

    public SinglePassSceneObjectsIndirectRenderer(@NotNull OpenGLRenderer openGLRenderer, @NotNull ShaderStorageBufferObject indirectSSBO, @NotNull ShaderStorageBufferObject propertiesSSBO, @NotNull Operator operator, @NotNull Pipeline pipeline, boolean usePropertiesSSBO, boolean useMaterialsSSBO) {
        super(openGLRenderer, indirectSSBO, propertiesSSBO, pipeline, usePropertiesSSBO, useMaterialsSSBO);
        this.operator = operator;
    }

    public void processAndRender(@Nullable ArbitraryArguments metaData) {
        if (!this.getRejected().isEmpty()) {
            this.getRejected().clear();
        }
        if (this.getIndirectMeshObjects() == null) {
            return;
        }
        IndirectBufferProgram renderBuffer = this.getOpenGLRenderer().getSceneIndirectBuffer();
        IntBuffer indexes = MemoryUtil.memAllocInt(SinglePassSceneObjectsIndirectRenderer.SSBO_DATASETS_ENT_IDS_SIZE);
        IntBuffer materialIds = MemoryUtil.memAllocInt(SinglePassSceneObjectsIndirectRenderer.SSBO_DATASETS_MATERIAL_IDS_SIZE);
        IndirectCommandsProgram indirectCommandsProgram = this.createCommands(this.getMode(), indexes, this.isUseMaterialsSSBO() ? materialIds : null, renderBuffer, this.getIndirectMeshObjects());
        this.fillSSBOWithSceneObjectInformation(indexes, materialIds, this.getIndirectMeshObjects());
        this.render(this.getOperator(), indirectCommandsProgram, renderBuffer, metaData);
        indirectCommandsProgram.destroyBuffer();
    }

    protected IndirectCommandsProgram createCommands(Mode mode, @NotNull IntBuffer indexes, IntBuffer materialIds, IndirectBufferProgram renderBuffer, Collection<SceneObject> sceneObjects) {
        DefaultIndirectCommandsProgram baseIndirectCommandProgram1 = new DefaultIndirectCommandsProgram(renderBuffer);
        baseIndirectCommandProgram1.createBuffer();
        baseIndirectCommandProgram1.buildCommands(indexes, materialIds, sceneObjects, mode);
        return baseIndirectCommandProgram1;
    }

    public @NotNull Operator getOperator() {
        return this.operator;
    }
}