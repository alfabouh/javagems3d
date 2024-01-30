package ru.BouH.engine.render.scene.fabric.models.base;

import org.jetbrains.annotations.NotNull;
import ru.BouH.engine.game.resources.assets.models.Model;
import ru.BouH.engine.game.resources.assets.models.formats.Format3D;
import ru.BouH.engine.game.resources.assets.shaders.ShaderManager;
import ru.BouH.engine.render.scene.fabric.constraints.ModelRenderConstraints;
import ru.BouH.engine.render.scene.preforms.RenderObjectData;

public abstract class RenderSceneObject implements IRenderSceneModel {
    private ShaderManager shaderManager;
    private Model<Format3D> model;
    private ModelRenderConstraints modelRenderConstraints;

    public RenderSceneObject(Model<Format3D> model, @NotNull ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        this.model = model;
        this.modelRenderConstraints = ModelRenderConstraints.defaultModelRenderConstraints();
    }

    public ModelRenderConstraints getModelRenderConstraints() {
        return this.modelRenderConstraints;
    }

    public RenderSceneObject setModelRenderConstraints(ModelRenderConstraints modelRenderConstraints) {
        this.modelRenderConstraints = modelRenderConstraints;
        return this;
    }

    public RenderSceneObject setModel(Model<Format3D> model) {
        this.model = model;
        return this;
    }

    public Model<Format3D> toRender() {
        return this.model;
    }

    public ShaderManager getShaderManager() {
        return this.shaderManager;
    }

    public RenderSceneObject setShaderManager(@NotNull ShaderManager shaderManager) {
        this.shaderManager = shaderManager;
        return this;
    }
}
