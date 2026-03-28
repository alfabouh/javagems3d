package api.scripting.coding.env.internal.util.world.render.world.instances.interfaces;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.render.lighting.JSLightI;
import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.objects.ILighted;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSSceneObjectWithLightsI", description = "Interface for objects that support attaching and managing lights")
public interface JSSceneObjectWithLightsI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java lighted object", paramNames = {})
    ILighted getJavaLightedObject();

    @JSCodingFunctionOrMethod(description = "Attach light to this object", paramNames = {"light"})
    default void addLight(@NotNull JSLightI light) {
        this.getJavaLightedObject().addLightAttachment((ILightAttached) light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Detach light from this object", paramNames = {"light"})
    default void removeLight(@NotNull JSLightI light) {
        this.getJavaLightedObject().removeLightAttachment((ILightAttached) light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Check if light is attached", paramNames = {"light"})
    default boolean hasLight(@NotNull JSLightI light) {
        return this.getJavaLightedObject().isLightAttached((ILightAttached) light.getJavaLight());
    }

    @JSCodingFunctionOrMethod(description = "Check if object has any lights", paramNames = {})
    default boolean hasLights() {
        return this.getJavaLightedObject().hasLights();
    }

    @JSCodingFunctionOrMethod(description = "Get position used for attaching lights", paramNames = {})
    default JSVector3f getLightAttachPosition() {
        return new JSVector3f(this.getJavaLightedObject().getPositionToAttachLights());
    }
}