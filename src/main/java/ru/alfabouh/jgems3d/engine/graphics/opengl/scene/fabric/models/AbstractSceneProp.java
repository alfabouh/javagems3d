package ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.models;

import org.jetbrains.annotations.NotNull;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.graphics.opengl.scene.objects.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class AbstractSceneProp implements IModeledSceneObject {
    private final IRenderFabric renderFabric;
    private Material overObjectMaterial;
    private Model<Format3D> model;
    private ModelRenderParams modelRenderParams;
    private boolean isVisible;

    public AbstractSceneProp(IRenderFabric renderFabric, Model<Format3D> model, @NotNull ModelRenderParams modelRenderParams) {
        this.renderFabric = renderFabric;
        this.model = model;
        this.modelRenderParams = modelRenderParams;
        this.isVisible = true;
        this.overObjectMaterial = null;
    }

    public AbstractSceneProp(IRenderFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        this.renderFabric = renderFabric;
        this.model = model;
        this.modelRenderParams = ModelRenderParams.defaultModelRenderConstraints(shaderManager);
        this.isVisible = true;
        this.overObjectMaterial = null;
    }

    @Override
    public RenderSphere calcRenderSphere() {
        if (!this.getModel().isValid()) {
            return null;
        }
        return new RenderSphere(this.getModel().getMeshDataGroup().calcDistanceToMostFarPoint(this.getModel().getFormat().getScaling()), this.getModel().getFormat().getPosition());
    }

    public Material getOverObjectMaterial() {
        return this.overObjectMaterial;
    }

    public AbstractSceneProp setOverObjectMaterial(Material overObjectMaterial) {
        this.overObjectMaterial = overObjectMaterial;
        return this;
    }

    public boolean canBeCulled() {
        return true;
    }

    public AbstractSceneProp setModelRenderConstraints(ModelRenderParams modelRenderParams) {
        this.modelRenderParams = modelRenderParams;
        return this;
    }

    public AbstractSceneProp setModel(Model<Format3D> model) {
        this.model = model;
        return this;
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    public ModelRenderParams getModelRenderParams() {
        return this.modelRenderParams;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public IRenderFabric renderFabric() {
        return this.renderFabric;
    }

    @Override
    public boolean hasRender() {
        return true;
    }
}
