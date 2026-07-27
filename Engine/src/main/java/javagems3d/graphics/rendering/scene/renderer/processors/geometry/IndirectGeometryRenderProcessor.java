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

package javagems3d.graphics.rendering.scene.renderer.processors.geometry;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.indirect.scene_objects.GroupedSceneObjectsIndirectRenderer;
import javagems3d.graphics.rendering.scene.renderer.processors.IRenderProcessor;
import javagems3d.graphics.screen.ticking.FrameTicking;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class IndirectGeometryRenderProcessor extends IRenderProcessor.Template {
    private final GroupedSceneObjectsIndirectRenderer indirectMeshObjects;
    private Consumer<JGemsShaderManager> uniformsHandler;

    public IndirectGeometryRenderProcessor(@Nullable Consumer<JGemsShaderManager> uniformsHandler, @NotNull ShaderStorageBufferObject indirectSSBO, @NotNull ShaderStorageBufferObject propertiesSSBO, @NotNull Pipeline pipeline, @NotNull OpenGLRenderer openGLRenderer) {
        super(openGLRenderer);
        this.indirectMeshObjects = new GroupedSceneObjectsIndirectRenderer(openGLRenderer, indirectSSBO, propertiesSSBO, pipeline, true, true);
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
        this.getRejected().clear();
    }

    @Override
    public void runProcessorRendering(FrameTicking frameTicking) {
        this.getIndirectMeshObjects().processAndRender(ArbitraryArguments.pass(this.getUniformsHandler()));
    }

    public void setIndirectMeshObjects(@NotNull Collection<SceneObject> sceneObjects) {
        this.getIndirectMeshObjects().setIndirectMeshObjects(sceneObjects);
    }

    public Set<SceneObject> getRejected() {
        return this.getIndirectMeshObjects().getRejected();
    }

    public void setUniformsHandler(Consumer<JGemsShaderManager> uniformsHandler) {
        this.uniformsHandler = uniformsHandler;
    }

    public Consumer<JGemsShaderManager> getUniformsHandler() {
        return this.uniformsHandler;
    }

    public GroupedSceneObjectsIndirectRenderer getIndirectMeshObjects() {
        return this.indirectMeshObjects;
    }
}