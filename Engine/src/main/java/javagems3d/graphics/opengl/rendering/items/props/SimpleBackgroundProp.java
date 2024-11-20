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

package javagems3d.graphics.opengl.rendering.items.props;

import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.environment.light.Light;
import javagems3d.graphics.opengl.rendering.fabric.objects.IRenderObjectFabric;
import javagems3d.graphics.opengl.rendering.items.AbstractSceneObject;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.old.properties.ModelRenderData;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleBackgroundProp extends AbstractSceneObject {
    private AnimationData animationData;
    private final IRenderObjectFabric renderFabric;
    private ModelRenderData modelRenderData;

    public SimpleBackgroundProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull ModelRenderData modelRenderData) {
        super(model);
        this.renderFabric = renderFabric;
        this.modelRenderData = modelRenderData;
        this.animationData = null;
    }

    public SimpleBackgroundProp(IRenderObjectFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, model, ModelRenderData.defaultMeshRenderData(shaderManager));
    }

    public void clearLights() {
    }

    public void addLight(Light light) {
    }

    public void removeLight(Light light) {
    }

    @Override
    public RenderAABB getRenderAABB() {
        if (!this.getModel().isValid()) {
            return null;
        }
        return JGemsHelper.UTILS.calcRenderAABBWithTransforms(this.getModel());
    }

    public SimpleBackgroundProp setModelRenderConstraints(ModelRenderData modelRenderData) {
        this.modelRenderData = modelRenderData;
        return this;
    }

    public boolean canBeCulled() {
        return true;
    }

    @Override
    public List<Light> getLightsList() {
        return null;
    }

    public SimpleBackgroundProp setModel(Model<Format3D> model) {
        this.model = model;
        this.initAnimation();
        return this;
    }

    public ModelRenderData getObjectRenderSettings() {
        return this.modelRenderData;
    }

    @Override
    public boolean isVisible() {
        return true;
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

    @Override
    public AnimationData getAnimationData() {
        return this.animationData;
    }

    @Override
    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }
}