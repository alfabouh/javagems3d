package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.resources.instances.models.animation.JSAnimationData;
import javagems3d.graphics.objects.IAnimated;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSAnimatedObject", description = "Represents animated object.")
public interface JSAnimatedObjectI {

    @JSCodingFunctionOrMethod(description = "Returns animation data of this object.", paramNames = {})
    JSAnimationData getAnimationData();

    @JSCodingFunctionOrMethod(description = "Sets animation by ID and returns updated animation data.", paramNames = {"id"})
    JSAnimationData setAnimationByID(int id);

    @JSCodingFunctionOrMethod(description = "Sets animation data for this object.", paramNames = {"animationData"})
    void setAnimationData(@NotNull JSAnimationData animationData);

    @JSCodingFunctionOrMethod(description = "Returns animation speed multiplier.", paramNames = {})
    default float animationSpeedMultiplier() {
        return 1.0f;
    }

    @JSCodingFunctionOrMethod(description = "Sets frame index of current animation data.", paramNames = {"i"})
    default void setAnimationDataFrame(int i) {
        this.getAnimationData().setFrame(i);
    }

    @JSCodingFunctionOrMethod(description = "Initializes animation to default ID (0) and returns animation data.", paramNames = {})
    default JSAnimationData initAnimation() {
        return this.setAnimationByID(0);
    }

    @JSCodingFunctionOrMethod(description = "Returns true if animation data exists and is valid.", paramNames = {})
    default boolean hasAnimationData() {
        return this.getAnimationData() != null && this.getAnimationData().isValid();
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java animated object.", paramNames = {})
    IAnimated getJavaAnimated();
}