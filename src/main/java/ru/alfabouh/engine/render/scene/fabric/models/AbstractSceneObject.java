package ru.alfabouh.engine.render.scene.fabric.models;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector3d;
import ru.alfabouh.engine.game.resources.assets.materials.Material;
import ru.alfabouh.engine.game.resources.assets.models.Model;
import ru.alfabouh.engine.game.resources.assets.models.formats.Format3D;
import ru.alfabouh.engine.game.resources.assets.shaders.ShaderManager;
import ru.alfabouh.engine.render.frustum.RenderABB;
import ru.alfabouh.engine.render.scene.SceneRender;
import ru.alfabouh.engine.render.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.engine.render.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.engine.render.scene.objects.IModeledSceneObject;

public abstract class AbstractSceneObject implements IModeledSceneObject {
    private final IRenderFabric renderFabric;
    private Material overObjectMaterial;
    private Model<Format3D> model;
    private ModelRenderParams modelRenderParams;
    private RenderABB renderABB;
    private boolean canBeCulled;
    private boolean isVisible;

    public AbstractSceneObject(IRenderFabric renderFabric, Model<Format3D> model, @NotNull ShaderManager shaderManager) {
        this.renderFabric = renderFabric;
        this.model = model;
        this.modelRenderParams = ModelRenderParams.defaultModelRenderConstraints(shaderManager);
        this.renderABB = new RenderABB();
        this.canBeCulled = false;
        this.isVisible = true;
        this.overObjectMaterial = null;
    }

    public Material getOverObjectMaterial() {
        return this.overObjectMaterial;
    }

    public AbstractSceneObject setOverObjectMaterial(Material overObjectMaterial) {
        this.overObjectMaterial = overObjectMaterial;
        return this;
    }

    public void setRenderAAB(Vector3d size) {
        this.renderABB = new RenderABB(this.model.getFormat().getPosition(), size);
        this.canBeCulled = true;
    }

    public boolean canBeCulled() {
        return this.canBeCulled;
    }

    public RenderABB getRenderABB() {
        return this.renderABB;
    }

    public Vector2d getTextureScaling() {
        return this.getModelRenderParams().getTextureScaling();
    }

    public void setTextureScaling(Vector2d textureScaling) {
        this.getModelRenderParams().setTextureScaling(textureScaling);
    }

    public AbstractSceneObject setModelRenderConstraints(ModelRenderParams modelRenderParams) {
        this.modelRenderParams = modelRenderParams;
        return this;
    }

    public AbstractSceneObject setModel(Model<Format3D> model) {
        this.model = model;
        return this;
    }

    public Model<Format3D> getModel3D() {
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