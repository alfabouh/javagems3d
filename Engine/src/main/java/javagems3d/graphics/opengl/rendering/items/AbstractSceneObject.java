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

package javagems3d.graphics.opengl.rendering.items;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.opengl.frustum.ICulled;
import javagems3d.graphics.opengl.rendering.items.settings.ObjectRenderSettings;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;

public abstract class AbstractSceneObject implements IModeled, IRendered, ICulled, ILightsKeeper {
    private float animationSpeed;
    protected Model<Format3D> model;
    private double lastTick;

    public AbstractSceneObject(Model<Format3D> model) {
        this.setModel(model);
        this.lastTick = JGems3D.glfwTime();
        this.animationSpeed = 1.0f;
    }

    public AbstractSceneObject() {
        this(null);
    }

    public AbstractSceneObject setModel(Model<Format3D> model) {
        this.model = model;
        this.initAnimation();
        return this;
    }

    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public void updateAnimation() {
        if (!this.hasAnimations()) {
            return;
        }
        double fps = 1.0f - this.getAnimationData().getCurrentAnimation().getDuration() / this.getAnimationData().getCurrentAnimation().getFrameCount();
        if (JGems3D.glfwTime() - this.lastTick >= fps * (1.0f / this.animationSpeedMultiplier())) {
            this.nextAnimationFrame();
            this.lastTick = JGems3D.glfwTime();
        }
    }

    public void nextAnimationFrame() {
        this.nextAnimationFrame(DefaultAttributePointers.ATTR_POSITIONS.getIndex());
    }

    public void nextAnimationFrame(int positionAttributeIndex) {
        if (this.hasAnimations()) {
            this.getAnimationData().nextFrame();
        }
    }

    @Override
    public float animationSpeedMultiplier() {
        return this.animationSpeed;
    }

    @Override
    public AnimationData setAnimationByID(int id) {
        if (!this.hasModel() || !this.hasAnimations()) {
            return null;
        }
        if (id < 0 || id >= this.getModel().getMeshStructure().getAnimationList().size()) {
            JGemsHelper.getLogger().error("Couldn't set animation for: " + this);
            return null;
        }
        AnimationData animationData = new AnimationData(this.getModel().getMeshStructure().getAnimationList().get(id));
        this.setAnimationData(animationData);
        this.nextAnimationFrame();
        return animationData;
    }

    public Model<Format3D> getModel() {
        return this.model;
    }

    public abstract ObjectRenderSettings getObjectRenderSettings();

    public boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }
}
