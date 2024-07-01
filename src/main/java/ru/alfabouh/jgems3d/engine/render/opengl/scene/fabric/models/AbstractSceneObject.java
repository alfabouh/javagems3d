package ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.models;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.render.opengl.frustum.RenderABB;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.base.IRenderFabric;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.fabric.render.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.render.opengl.scene.objects.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;

public abstract class AbstractSceneObject implements IModeledSceneObject {
    private final IRenderFabric renderFabric;
    private Material overObjectMaterial;
    private Model<Format3D> model;
    private ModelRenderParams modelRenderParams;
    private RenderABB renderABB;
    private boolean canBeCulled;
    private boolean isVisible;

    public AbstractSceneObject(IRenderFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
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

    public void setRenderAAB(Vector3f size) {
        this.renderABB = new RenderABB(this.model.getFormat().getPosition(), size);
        this.canBeCulled = true;
    }

    public boolean canBeCulled() {
        return this.canBeCulled;
    }

    public RenderABB getRenderABB() {
        return this.renderABB;
    }

    public Vector2f getTextureScaling() {
        return this.getModelRenderParams().getTextureScaling();
    }

    public void setTextureScaling(Vector2f textureScaling) {
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
