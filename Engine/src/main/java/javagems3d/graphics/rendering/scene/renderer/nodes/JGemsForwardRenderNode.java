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

package javagems3d.graphics.rendering.scene.renderer.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.ForwardRenderNode;
import javagems3d.help.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.resources.managing.JGemsResourceManager;
import org.jetbrains.annotations.NotNull;

public class JGemsForwardRenderNode extends ForwardRenderNode {

    public JGemsForwardRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
    }

    @Override
    public @NotNull ITexture2DProgram getAnimationsTexture() {
        return JGemsHelper.resources().getAnimationsTextureBuffer();
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return JGemsResourceManager.globalShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return JGemsResourceManager.globalShaderAssets.MainScenePropertiesData;
    }

    @Override
    public boolean renderBackground() {
        return true;
    }

    @Override
    public @NotNull JGemsShaderManager getSkyBoxShader() {
        return JGemsResourceManager.globalShaderAssets.skybox;
    }
}