package api.scripting.coding.env.internal.util.world.render.world.instances;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimationData;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSAnimatedObjectI;
import javagems3d.graphics.objects.IAnimated;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneAnimatedObject", description = "Wrapper for animated scene object.")
public class JSSceneAnimatedObject implements JSAnimatedObjectI {
    @JSHideFromDoc
    private final IAnimated animated;

    @JSCodingConstructor(description = "Wraps animated object.", paramNames = {"animated"})
    public JSSceneAnimatedObject(IAnimated animated) {
        this.animated = animated;
    }

    @Override
    public JSAnimationData getAnimationData() {
        return new JSAnimationData(this.animated.getAnimationData());
    }

    @Override
    public JSAnimationData setAnimationByID(int id) {
        return new JSAnimationData(this.animated.setAnimationByID(id));
    }

    @Override
    public void setAnimationData(@NotNull JSAnimationData animationData) {
        this.animated.setAnimationData(animationData.getJavaAnimationData());
    }

    @JSCodingFunctionOrMethod(description = "Returns animation speed multiplier.")
    public float getSpeedMultiplier() {
        return this.animated.animationSpeedMultiplier();
    }

    @Override
    @JSHideFromDoc
    public IAnimated getJavaAnimated() {
        return this.animated;
    }
}