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

import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.formats.Format3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SimpleBackgroundProp extends SceneObject {
    private AnimationData animationData;

    public SimpleBackgroundProp(Model<Format3D> model, @Nullable RenderAttributes renderAttributes) {
        super(model, renderAttributes);
        this.animationData = null;
    }

    public SimpleBackgroundProp(Model<Format3D> model, @Nullable RenderTable renderTable) {
        this(model, renderTable != null ? new RenderAttributes(renderTable) : null);
    }

    public void clearLights() {
    }

    public void addLight(Light light) {
    }

    public void removeLight(Light light) {
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

    @Override
    public boolean canBeRendered() {
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