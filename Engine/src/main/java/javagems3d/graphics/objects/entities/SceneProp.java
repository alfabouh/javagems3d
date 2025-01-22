package javagems3d.graphics.objects.entities;

import javagems3d.JGemsHelper;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.formats.Format3D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SceneProp extends SceneObject implements IWorldObject, IWorldTicked {
    private final List<Light> lightList;
    private boolean isVisible;
    private boolean isDead;

    public SceneProp(@NotNull SceneWorld sceneWorld, @Nullable Model<Format3D> model, @NotNull RenderAttributes objectRenderingConfiguration) {
        super(sceneWorld, model, objectRenderingConfiguration);
        this.lightList = new ArrayList<>();
        this.isVisible = true;
        this.isDead = false;
    }

    public void clearLights() {
        Iterator<Light> lightIterator = this.getLightsList().iterator();
        while (lightIterator.hasNext()) {
            Light l = lightIterator.next();
            l.off();
            this.onRemoveLight(l);
            lightIterator.remove();
        }
    }

    public void addLight(Light light) {
        this.getLightsList().add(light);
        light.on();
        this.onAddLight(light);
    }

    public void removeLight(Light light) {
        this.getLightsList().remove(light);
        light.off();
        this.onRemoveLight(light);
    }

    protected void onAddLight(Light light) {
        JGemsHelper.getLogger().log("Add light to: " + this);
    }

    protected void onRemoveLight(Light light) {
        JGemsHelper.getLogger().log("Removed light from: " + this);
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

    public void setDead() {
        this.isDead = true;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public List<Light> getLightsList() {
        return this.lightList;
    }

    @Override
    public boolean isDead() {
        return this.isDead;
    }

    @Override
    public boolean canBeRendered() {
        return super.canBeRendered() && this.isVisible;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " - " + this.getModel().getFormat().getPosition();
    }
}
