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
import javagems3d.graphics.objects.rendering.pipeline.enums.Pipeline;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.args.ArbitraryArguments;
import logger.Log;
import org.jetbrains.annotations.NotNull;

public abstract class DirectRenderFabric implements IRenderFabric {
    private final Stage stage;

    public DirectRenderFabric(@NotNull Stage stage) {
        if (!stage.getType().equals(Type.DIRECT)) {
            Log.get().warn("RenderFabric type doesn't belong to DIRECT");
        }
        this.stage = stage;
    }

    public abstract void onPreRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData);
    public abstract void onRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData);
    public abstract void onPostRender(Pipeline pipeline, JGemsShaderManager shaderManager, OpenGLRenderer openGLRenderer, IRendered renderedItem, ArbitraryArguments metaData);

    public @NotNull Stage getRenderingStage() {
        return this.stage;
    }
}