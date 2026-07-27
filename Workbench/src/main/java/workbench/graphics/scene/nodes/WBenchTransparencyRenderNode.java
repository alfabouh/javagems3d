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

package workbench.graphics.scene.nodes;

import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.programs.textures.base.ITexture2DProgram;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.templates.abstractions.TransparencyRenderNode;
import javagems3d.system.resources.assets.shaders.buffers.ShaderStorageBufferObject;
import org.jetbrains.annotations.NotNull;
import workbench.resources.WBenchResourceManager;

public class WBenchTransparencyRenderNode extends TransparencyRenderNode {
    public WBenchTransparencyRenderNode(@NotNull FBOTexture2DProgram inColor, OpenGLRenderer openGLRenderer) {
        super(inColor, openGLRenderer);
    }

    @Override
    public @NotNull ITexture2DProgram getAnimationsTexture() {
        return WBenchResourceManager.getAnimationsTextureBuffer();
    }

    @Override
    public @NotNull ShaderStorageBufferObject getIndirectBufferData() {
        return WBenchResourceManager.localShaderAssets.MainSceneIndirectBufferData;
    }

    @Override
    public @NotNull ShaderStorageBufferObject getPropertiesData() {
        return WBenchResourceManager.localShaderAssets.MainScenePropertiesData;
    }
}
