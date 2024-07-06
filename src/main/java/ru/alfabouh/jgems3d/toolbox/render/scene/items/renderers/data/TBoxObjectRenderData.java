package ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

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
