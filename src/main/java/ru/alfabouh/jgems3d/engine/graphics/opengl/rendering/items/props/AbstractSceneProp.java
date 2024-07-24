package ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.props;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import ru.alfabouh.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.data.ModelRenderParams;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import ru.alfabouh.jgems3d.engine.graphics.opengl.rendering.items.IModeledSceneObject;
import ru.alfabouh.jgems3d.engine.physics.world.IWorld;
import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.alfabouh.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.alfabouh.jgems3d.engine.system.JGemsHelper;
import ru.alfabouh.jgems3d.engine.system.resources.assets.materials.Material;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.Model;
import ru.alfabouh.jgems3d.engine.system.resources.assets.models.formats.Format3D;
import ru.alfabouh.jgems3d.engine.system.resources.assets.shaders.manager.JGemsShaderManager;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSceneProp implements IModeledSceneObject, IWorldObject, IWorldTicked {
    private final List<Light> lightList;
    private final IRenderObjectFabric renderFabric;
    private Material overObjectMaterial;
    private Model<Format3D> model;
    private ModelRenderParams modelRenderParams;
    private boolean isVisible;

    public AbstractSceneProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull ModelRenderParams modelRenderParams) {
        this.lightList = new ArrayList<>();

        this.renderFabric = renderFabric;
        this.model = model;
        this.modelRenderParams = modelRenderParams;
        this.isVisible = true;
        this.overObjectMaterial = null;
    }

    public AbstractSceneProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, model, ModelRenderParams.defaultModelRenderConstraints(shaderManager));
    }

    public void clearLights() {
        Iterator<Light> lightIterator = this.getLightList().iterator();
        while (lightIterator.hasNext()) {
            Light l = lightIterator.next();
            l.stop();
            this.onRemoveLight(l);
            lightIterator.remove();
        }
    }

    public void addLight(Light light) {
        this.getLightList().add(light);
        light.start();
        this.onAddLight(light);
    }

    public void removeLight(Light light) {
        this.getLightList().remove(light);
        light.stop();
        this.onRemoveLight(light);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        SystemLogging.get().getLogManager().log("[ " + this + " ]" + " - PreRender");
        if (this.hasRender()) {
            this.renderFabric().onStartRender(this);
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        SystemLogging.get().getLogManager().log("[ " + this + " ]" + " - PostRender");
        if (this.hasRender()) {
            this.renderFabric().onStopRender(this);
        }
        this.clearLights();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.adjustLightsTranslation(this.getModel().getFormat().getPosition(), new Vector3f(0.0f));
    }

    protected void onAddLight(Light light) {
        SystemLogging.get().getLogManager().log("Add light to: " + this);
    }

    protected void onRemoveLight(Light light) {
        SystemLogging.get().getLogManager().log("Removed light from: " + this);
    }

    @Override
    public RenderSphere calcRenderSphere() {
        if (!this.getModel().isValid()) {
            return null;
        }
        return new RenderSphere(JGemsHelper.calcDistanceToMostFarPoint(this.getModel().getMeshDataGroup(), this.getModel().getFormat().getScaling()), this.getModel().getFormat().getPosition());
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public Material getOverObjectMaterial() {
        return this.overObjectMaterial;
    }

    public AbstractSceneProp setOverObjectMaterial(Material overObjectMaterial) {
        this.overObjectMaterial = overObjectMaterial;
        return this;
    }

    public AbstractSceneProp setModelRenderConstraints(ModelRenderParams modelRenderParams) {
        this.modelRenderParams = modelRenderParams;
        return this;
    }

    public AbstractSceneProp setModel(Model<Format3D> model) {
        this.model = model;
        return this;
    }

    public boolean canBeCulled() {
        return true;
    }

    @Override
    public List<Light> getLightList() {
        return this.lightList;
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

    @Override
    public IRenderObjectFabric renderFabric() {
        return this.renderFabric;
    }

    @Override
    public boolean hasRender() {
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - " + this.getModel().getFormat().getPosition();
    }
}
