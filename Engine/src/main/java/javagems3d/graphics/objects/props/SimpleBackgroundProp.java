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

package javagems3d.graphics.objects.props;

import javagems3d.graphics.environment.lighting.Light;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.ObjectRenderConfiguration;
import javagems3d.graphics.objects.rendering.fabric.IRenderFabric;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleBackgroundProp extends SceneObject {
    private AnimationData animationData;
    private final IRenderFabric renderFabric;
    private ObjectRenderConfiguration objectRenderingConfiguration;

    public SimpleBackgroundProp(IRenderFabric renderFabric, Model<Format3D> model, @NotNull ObjectRenderConfiguration objectRenderingConfiguration) {
        super(model);
        this.renderFabric = renderFabric;
        this.objectRenderingConfiguration = objectRenderingConfiguration;
        this.animationData = null;
    }

    public SimpleBackgroundProp(IRenderFabric renderFabric, Model<Format3D> model, @NotNull JGemsShaderManager shaderManager) {
        this(renderFabric, model, new ObjectRenderConfiguration(shaderManager));
    }

    public void clearLights() {
    }

    public void addLight(Light light) {
    }

    public void removeLight(Light light) {
    }

    public SimpleBackgroundProp setModelRenderConstraints(ObjectRenderConfiguration objectRenderingConfiguration) {
        this.objectRenderingConfiguration = objectRenderingConfiguration;
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

    public @NotNull ObjectRenderConfiguration getObjectRenderConfiguration() {
        return this.objectRenderingConfiguration;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public IRenderFabric getRenderFabric() {
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