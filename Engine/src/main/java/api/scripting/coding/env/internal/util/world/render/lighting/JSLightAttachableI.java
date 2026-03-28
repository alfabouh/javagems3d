package api.scripting.coding.env.internal.util.world.render.lighting;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.objects.ILighted;

@JSCodingClass(binding = "JSLightAttachableI", description = "Interface for lights that can be attached to scene objects")
public interface JSLightAttachableI {

    @JSCodingFunctionOrMethod(description = "Get underlying Java light attachment object")
    ILightAttached getJavaLightAttached();

    @JSCodingFunctionOrMethod(description = "Attach this light to scene object", paramNames = {"object"})
    default void attachTo(JSSceneObjectWithLightsI object) {
        this.getJavaLightAttached().attachTo(object == null ? null : object.getJavaLightedObject());
    }

    @JSCodingFunctionOrMethod(description = "Detach this light")
    default void detach() {
        this.getJavaLightAttached().detach();
    }

    @JSCodingFunctionOrMethod(description = "Get attached object")
    default JSSceneObjectWithLightsI getAttachedTo() {
        ILighted lighted = this.getJavaLightAttached().getAttachedTo();
        if (lighted == null) return null;

        return () -> lighted;
    }

    @JSCodingFunctionOrMethod(description = "Get action on detach")
    default JSActionOnDetach getActionOnDetach() {
        return JSActionOnDetach.valueOf(this.getJavaLightAttached().getActionOnDeath().name());
    }
}