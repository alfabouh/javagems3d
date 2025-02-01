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

package javagems3d.graphics.objects;

import javagems3d.JGems3D;
import javagems3d.JGemsHelper;
import javagems3d.graphics.objects.rendering.configuration.RenderAttributes;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;

public abstract class SceneObject implements IModeled, IRendered, ILighted {
    private AnimationData animationData;
    private final SceneWorld sceneWorld;
    private RenderAttributes renderAttributes;
    protected Model<Format3D> model;
    private float animationSpeed;
    private double lastTick;

    private CullingAABB cullingAABB;

    public SceneObject(SceneWorld sceneWorld, Model<Format3D> model, RenderAttributes renderAttributes) {
        this.setModel(model);
        this.sceneWorld = sceneWorld;
        this.renderAttributes = renderAttributes;
        this.lastTick = JGems3D.glfwTime();
        this.animationSpeed = 1.0f;
        this.animationData = null;
        this.cullingAABB = null;
    }

    public SceneObject setModel(Model<Format3D> model) {
        this.model = model;
        this.initAnimation();
        this.setCullingData(this.pickAABBDataFromMesh());
        return this;
    }

    @Override
    public CullingAABB getCullingData() {
        return this.cullingAABB;
    }

    @Override
    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
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

    public void setRenderAttributes(RenderAttributes renderAttributes) {
        this.renderAttributes = renderAttributes;
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

    public SceneObject setCullingData(CullingAABB cullingAABB) {
        this.cullingAABB = cullingAABB;
        return this;
    }

    @Override
    public AnimationData getAnimationData() {
        return this.animationData;
    }

    public SceneWorld getSceneWorld() {
        return this.sceneWorld;
    }

    @Override
    public RenderAttributes getRenderAttributes() {
        return this.renderAttributes;
    }

    @Override
    public float animationSpeedMultiplier() {
        return this.animationSpeed;
    }

    public Model<Format3D> getModel() {
        return this.model;
    }
}
