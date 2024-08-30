/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.toolbox.render.scene.items.renderers.data;

import org.jetbrains.annotations.NotNull;
import javagems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import javagems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public final class TBoxObjectRenderData {
    private final TBoxShaderManager shaderManager;
    private final ITBoxObjectRenderer objectRenderer;

    public TBoxObjectRenderData(@NotNull TBoxShaderManager shaderManager, @NotNull ITBoxObjectRenderer objectRenderer) {
        this.shaderManager = shaderManager;
        this.objectRenderer = objectRenderer;
    }

    public TBoxShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public ITBoxObjectRenderer getObjectRenderer() {
        return this.objectRenderer;
    }
}
