package ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.data;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.ShaderManager;
import ru.alfabouh.jgems3d.toolbox.render.scene.items.renderers.ITBoxObjectRenderer;
import ru.alfabouh.jgems3d.toolbox.resources.shaders.manager.TBoxShaderManager;

public final class TBoxObjectRenderData {
    private TBoxShaderManager shaderManager;
    private ITBoxObjectRenderer objectRenderer;

    public TBoxObjectRenderData(@NotNull TBoxShaderManager shaderManager, @NotNull ITBoxObjectRenderer objectRenderer) {
        this.shaderManager = shaderManager;
        this.objectRenderer = objectRenderer;
    }

    public void setObjectRenderer(ITBoxObjectRenderer objectRenderer) {
        this.objectRenderer = objectRenderer;
    }

    public void setShaderManager(TBoxShaderManager shaderManager) {
        this.shaderManager = shaderManager;
    }

    public TBoxShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public ITBoxObjectRenderer getObjectRenderer() {
        return this.objectRenderer;
    }
}
