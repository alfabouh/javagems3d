/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package api.scripting.coding.env.internal.util.world.render.lighting;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneEntityI;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSSceneObjectWithLightsI;
import javagems3d.graphics.environment.lights.ILightAttachable;
import javagems3d.graphics.environment.lights.Light;
import javagems3d.graphics.environment.lights.PointLight;
import javagems3d.graphics.objects.IObjectWithLights;
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
        return this.pointLight.getActionOnDeath().equals(ILightAttachable.ActionOnDetach.DESTROY) ? JSActionOnDetach.DESTROY : JSActionOnDetach.KEEP_IN_WORLD;
    }

    @Override
    public ILightAttachable getJavaLightAttached() {
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Attach the light to a scene object implementing JSSceneObjectWithLightsI")
    public void attachTo(@NotNull JSSceneObjectWithLightsI sceneObject) {
        this.pointLight.attachTo(sceneObject.getJavaLightedObject());
    }

    @JSCodingFunctionOrMethod(description = "Get the scene object this light is attached to")
    public JSSceneObjectWithLightsI getAttachedTo() {
        IObjectWithLights attached = this.pointLight.getAttachedTo();
        if (attached == null) return null;
        return () -> attached;
    }

    @JSCodingFunctionOrMethod(description = "Check if the light is active")
    public boolean isActive() {
        return this.pointLight.isActive();
    }

    @Override
    public PointLight getJavaLight() {
        return this.pointLight;
    }
}