package api.scripting.coding.env.internal.util.world.render.lighting;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneEntityI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import javagems3d.graphics.environment.lights.ILightAttached;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.objects.ILighted;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(
        binding = "JSPointLight",
        description = "Wrapper for PointLight. Controls position, color, brightness, offset, and attachment to scene objects."
)
public class JSPointLight implements JSLightI, JSLightAttachableI {

    @JSCodingField(description = "Underlying Java PointLight object")
    private final PointLight pointLight;

    @JSCodingConstructor(description = "Create a new PointLight with default values")
    public JSPointLight() {
        this.pointLight = new PointLight();
    }

    @JSCodingConstructor(description = "Wrap an existing PointLight instance")
    public JSPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    @JSCodingConstructor(description = "Create PointLight with position, color, and offset")
    public JSPointLight(@NotNull JSVector3f position, @NotNull JSVector3f color, @NotNull JSVector3f offset) {
        this.pointLight = new PointLight(position.getJavaVector3f(), color.getJavaVector3f(), offset.getJavaVector3f());
    }

    @JSCodingConstructor(description = "Create PointLight attached to a scene entity")
    public JSPointLight(@NotNull JSSceneEntityI sceneEntityI) {
        this.pointLight = new PointLight(sceneEntityI.getJavaSceneEntity());
    }

    @JSCodingFunctionOrMethod(description = "Enable the light")
    public JSPointLight on() {
        this.pointLight.on();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Disable the light")
    public JSPointLight off() {
        this.pointLight.off();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set the light's brightness")
    public JSPointLight setBrightness(float brightness) {
        this.pointLight.setBrightness(brightness);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the light's brightness")
    public float getBrightness() {
        return this.pointLight.getBrightness();
    }

    @JSCodingFunctionOrMethod(description = "Set the light's color")
    public JSPointLight setColor(@NotNull JSVector3f color) {
        this.pointLight.setLightColor(color.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the light's color")
    public @NotNull JSVector3f getColor() {
        return new JSVector3f(this.pointLight.getLightColor());
    }

    @JSCodingFunctionOrMethod(description = "Set the light's position")
    public JSPointLight setPosition(@NotNull JSVector3f position) {
        this.pointLight.setLightPosition(position.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the light's position")
    public @NotNull JSVector3f getPosition() {
        return new JSVector3f(this.pointLight.getLightPosition());
    }

    @JSCodingFunctionOrMethod(description = "Set the light's offset")
    public JSPointLight setOffset(@NotNull JSVector3f offset) {
        this.pointLight.setOffset(offset.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the light's offset")
    public @NotNull JSVector3f getOffset() {
        return new JSVector3f(this.pointLight.getOffset());
    }

    @JSCodingFunctionOrMethod(description = "Set action on detach")
    public JSPointLight setActionOnDetach(PointLight.ActionOnDetach action) {
        this.pointLight.setActionOnDetach(action);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get action on detach")
    public JSActionOnDetach getActionOnDetach() {
        return this.pointLight.getActionOnDeath().equals(ILightAttached.ActionOnDetach.DESTROY) ? JSActionOnDetach.DESTROY : JSActionOnDetach.KEEP_IN_WORLD;
    }

    @Override
    public ILightAttached getJavaLightAttached() {
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Attach the light to a scene object implementing JSSceneObjectWithLightsI")
    public void attachTo(@NotNull JSSceneObjectWithLightsI sceneObject) {
        this.pointLight.attachTo(sceneObject.getJavaLightedObject());
    }

    @JSCodingFunctionOrMethod(description = "Get the scene object this light is attached to")
    public JSSceneObjectWithLightsI getAttachedTo() {
        ILighted attached = this.pointLight.getAttachedTo();
        if (attached == null) return null;
        return () -> attached;
    }

    @JSCodingFunctionOrMethod(description = "Check if the light is active")
    public boolean isActive() {
        return this.pointLight.isActive();
    }

    @Override
    public Light getJavaLight() {
        return this.pointLight;
    }
}