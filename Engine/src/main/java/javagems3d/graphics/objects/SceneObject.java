package javagems3d.graphics.objects;

import javagems3d.JGems3D;
import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.world.IRenderWorld;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.system.global.JGemsConfig;
import javagems3d.graphics.objects.rendering.attributes.RenderAttributes;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.animation.AnimationData;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class SceneObject implements IModeled, IRendered, ILighted, IWorldObject {
    private AnimationData animationData;
    private final IRenderWorld world;
    private RenderAttributes renderAttributes;
    protected Model3D model;
    private float animationSpeed;
    private float animationProgress;
    private double lastTick;
    private CullingAABB cullingAABB;
    private final Set<ILightAttached> lights;

    public SceneObject(IRenderWorld world, Model3D model, RenderAttributes renderAttributes) {
        this.animationData = null;
        this.cullingAABB = null;
        this.setModel(model);
        this.world = world;
        this.renderAttributes = renderAttributes;
        this.lastTick = JGems3D.glfwTime();
        this.animationSpeed = 1.0f;
        this.lights = new HashSet<>();
    }

    public SceneObject setModel(Model3D model) {
        this.model = model;
        this.initAnimation();
        this.setCullingData(this.pickAABBDataFromMesh());
        return this;
    }

    public void clearLights() {
        for (ILightAttached lightAttached : this.getAttachedLights()) {
            Light light = (Light) lightAttached;
            switch (lightAttached.getActionOnDeath()) {
                case DESTROY: {
                    this.getWorld().getEnvironment().getLightScene().removeLight(light);
                    break;
                }
                case KEEP_IN_WORLD: {
                    this.removeLight(lightAttached);
                    break;
                }
            }
        }
        this.getAttachedLights().clear();
    }

    @Override
    public void onSpawn(IWorld iWorld) {

    }

    @Override
    public void onDestroy(IWorld iWorld) {
        this.clearLights();
    }

    @Override
    public void setAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    public void setAnimationSpeed(float animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public void updateAnimation() {
        if (!this.hasAnimationData()) {
            return;
        }
        double fps = this.getAnimationData().getCurrentAnimation().getFps();
        if (fps <= 0.0d) {
            fps = JGemsConfig.SYSTEM.DEFAULT_ANIM_FPS;
        }
        fps *= this.animationSpeedMultiplier();
        double deltaTime = JGems3D.glfwTime() - this.lastTick;
        this.animationProgress += (float) (deltaTime * fps);
        if (this.animationProgress >= 1.0f) {
            this.nextAnimationFrame();
            this.animationProgress %= 1.0f;
        }
        this.getAnimationData().setAnimationFrameDelta(1.0f - this.animationProgress);
        this.lastTick = JGems3D.glfwTime();
    }

    public void nextAnimationFrame() {
        if (this.hasAnimationData()) {
            this.getAnimationData().nextFrame();
        }
    }

    public void setRenderAttributes(RenderAttributes renderAttributes) {
        this.renderAttributes = renderAttributes;
    }

    @Override
    public AnimationData setAnimationByID(int id) {
        if (!this.hasModel() || !this.getModel().getMeshStructure().isAnimatedStructure()) {
            return null;
        }
        if (id < 0 || id >= this.getModel().getMeshStructure().getAnimationsList().size()) {
            Log.get().error("Couldn't set animation for: " + this);
            return null;
        }
        AnimationData animationData = new AnimationData(this.getModel().getMeshStructure().getAnimationsList().get(id));
        this.setAnimationData(animationData);
        this.nextAnimationFrame();
        return animationData;
    }

    public void setCullingData(CullingAABB cullingAABB) {
        this.cullingAABB = cullingAABB;
    }

    @Override
    public @NotNull Set<ILightAttached> getAttachedLights() {
        return this.lights;
    }

    @Override
    public CullingAABB getCullingData() {
        return this.cullingAABB;
    }

    @Override
    public AnimationData getAnimationData() {
        return this.animationData;
    }

    public IRenderWorld getWorld() {
        return this.world;
    }

    @Override
    public RenderAttributes getRenderAttributes() {
        return this.renderAttributes;
    }

    @Override
    public float animationSpeedMultiplier() {
        return this.animationSpeed;
    }

    public Model3D getModel() {
        return this.model;
    }
}
