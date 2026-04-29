package api.scripting.coding.env.internal.util.resources.instances.models.animation;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationData;

@JSCodingClass(binding = "JSAnimationData", description = "Controls animation playback, frame switching and interpolation")
public class JSAnimationData {

    @JSHideFromDoc
    private final AnimationData animationData;

    @JSCodingConstructor(description = "Create empty animation data")
    public JSAnimationData() {
        this.animationData = new AnimationData();
    }

    @JSCodingConstructor(description = "Wrap existing AnimationData")
    public JSAnimationData(AnimationData animationData) {
        this.animationData = animationData;
    }

    @JSCodingConstructor(description = "Create animation data with animation")
    public JSAnimationData(JSAnimation animation) {
        this.animationData = new AnimationData(animation == null ? null : animation.getJavaAnimation());
    }

    @JSCodingFunctionOrMethod(description = "Go to next animation frame")
    public void nextFrame() {
        this.animationData.nextFrame();
    }

    @JSCodingFunctionOrMethod(description = "Set current frame index")
    public void setFrame(int frame) {
        this.animationData.setFrame(frame);
    }

    @JSCodingFunctionOrMethod(description = "Get current frame index")
    public int getCurrentFrameId() {
        return this.animationData.getCurrentFrameId();
    }

    @JSCodingFunctionOrMethod(description = "Get previous frame index")
    public int getPreviousFrameId() {
        return this.animationData.getPreviousFrameId();
    }

    @JSCodingFunctionOrMethod(description = "Get animation FPS")
    public double getFps() {
        return this.animationData.getFps();
    }

    @JSCodingFunctionOrMethod(description = "Get interpolation delta between frames")
    public float getFrameDelta() {
        return this.animationData.getAnimationFrameDelta();
    }

    @JSCodingFunctionOrMethod(description = "Set interpolation delta between frames")
    public void setFrameDelta(float delta) {
        this.animationData.setAnimationFrameDelta(delta);
    }

    @JSCodingFunctionOrMethod(description = "Set current animation")
    public void setAnimation(JSAnimation animation) {
        this.animationData.setAnimation(animation == null ? null : animation.getJavaAnimation());
    }

    @JSCodingFunctionOrMethod(description = "Get current animation")
    public JSAnimation getAnimation() {
        Animation anim = this.animationData.getCurrentAnimation();
        return anim == null ? null : new JSAnimation(anim);
    }

    @JSCodingFunctionOrMethod(description = "Check if animation is valid")
    public boolean isValid() {
        return this.animationData.isValid();
    }

    @JSHideFromDoc
    public AnimationData getJavaAnimationData() {
        return this.animationData;
    }
}