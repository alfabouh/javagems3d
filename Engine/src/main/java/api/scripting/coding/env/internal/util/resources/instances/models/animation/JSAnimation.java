package api.scripting.coding.env.internal.util.resources.instances.models.animation;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;

import java.util.List;

@JSCodingClass(binding = "JSAnimation", description = "...")
public class JSAnimation {
    @JSHideFromDoc private Animation animation;

    @JSHideFromDoc
    public JSAnimation(Animation animation) {
        this.animation = animation;
    }

    @JSHideFromDoc
    public JSAnimation(String name, double duration, double fps, List<JSAnimationFrame> frameList) {
        this.animation = new Animation(name, duration, fps, frameList.stream().map(JSAnimationFrame::getJavaAnimationFrame).toList());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public String name() {
        return this.animation.name();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public double duration() {
        return this.animation.duration();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public double fps() {
        return this.animation.fps();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public List<JSAnimationFrame> getFrameList() {
        return this.animation.frameList().stream().map(JSAnimationFrame::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "...")
    public Animation getJavaAnimation() {
        return this.animation;
    }
}
