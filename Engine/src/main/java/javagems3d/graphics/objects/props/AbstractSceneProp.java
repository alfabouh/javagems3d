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

import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.objects.rendering.pipeline.RenderTable;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractSceneProp extends SceneObject implements IWorldObject, IWorldTicked {
    private AnimationData animationData;
    private final List<Light> lightList;
    private RenderAttributes objectRenderingConfiguration;
    private boolean isVisible;

    public AbstractSceneProp(Model<Format3D> model, @NotNull RenderAttributes objectRenderingConfiguration) {
        super(model, objectRenderingConfiguration);
        this.lightList = new ArrayList<>();

        this.animationData = null;
        this.objectRenderingConfiguration = objectRenderingConfiguration;
        this.isVisible = true;
    }

    public AbstractSceneProp(Model<Format3D> model, @Nullable RenderTable renderTable) {
        this(model, new RenderAttributes(renderTable));
    }

    public void clearLights() {
        Iterator<Light> lightIterator = this.getLightsList().iterator();
        while (lightIterator.hasNext()) {
            Light l = lightIterator.next();
            l.stop();
            this.onRemoveLight(l);
            lightIterator.remove();
        }
    }

    public void addLight(Light light) {
        this.getLightsList().add(light);
        light.start();
        this.onAddLight(light);
    }

    public void removeLight(Light light) {
        this.getLightsList().remove(light);
        light.stop();
        this.onRemoveLight(light);
    }

    @Override
    public void onSpawn(IWorld iWorld) {
        JGemsHelper.getLogger().log("[ " + this + " ]" + " - PreRender");
        if (this.canBeRendered()) {
            this.getRenderFabricsSet().forEach(e -> e.createResources(this));
        }
    }

    @Override
    public void onDestroy(IWorld iWorld) {
        JGemsHelper.getLogger().log("[ " + this + " ]" + " - PostRender");
        if (this.canBeRendered()) {
            this.getRenderFabricsSet().forEach(e -> e.destroyResources(this));
        }
        this.clearLights();
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        this.adjustLightsTranslation(this.getModel().getFormat().getPosition(), new Vector3f(0.0f));
    }

    protected void onAddLight(Light light) {
        JGemsHelper.getLogger().log("Add light to: " + this);
    }

    protected void onRemoveLight(Light light) {
        JGemsHelper.getLogger().log("Removed light from: " + this);
    }

    public AbstractSceneProp setModelRenderConstraints(RenderAttributes objectRenderingConfiguration) {
        this.objectRenderingConfiguration = objectRenderingConfiguration;
        return this;
    }

    public boolean canBeCulled() {
        return true;
    }

    @Override
    public List<Light> getLightsList() {
        return this.lightList;
    }

    public @NotNull RenderAttributes getRenderAttributes() {
        return this.objectRenderingConfiguration;
    }

    @Override
    public boolean canBeRendered() {
        return super.canBeRendered() && this.isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public AnimationData getAnimationData() {
        return this.animationData;
    }

    @Override
    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - " + this.getModel().getFormat().getPosition();
    }
}
