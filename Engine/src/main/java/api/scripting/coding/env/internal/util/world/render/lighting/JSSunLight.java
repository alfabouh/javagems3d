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
import javagems3d.graphics.environment.lights.SunLight;

@JSCodingClass(binding = "JSSunLight", description = "Wrapper for SunLight. Controls position, color, and brightness of a directional sun light.")
public class JSSunLight implements JSLightI {
    @JSCodingField(description = "Underlying Java SunLight object")
    private final SunLight sunLight;

    @JSCodingConstructor(description = "Create a new SunLight with default position (0,0,0), color (1,1,1), and brightness 1.0")
    public JSSunLight() {
        this.sunLight = new SunLight(new JSVector3f().getJavaVector3f(), new JSVector3f(1.0f, 1.0f, 1.0f).getJavaVector3f(), 1.0f);
    }

    @JSCodingConstructor(description = "Wrap an existing SunLight instance")
    public JSSunLight(SunLight sunLight) {
        this.sunLight = sunLight;
    }

    @JSCodingConstructor(description = "Create a new SunLight with given position, color, and brightness")
    public JSSunLight(JSVector3f position, JSVector3f color, float brightness) {
        this.sunLight = new SunLight(position.getJavaVector3f(), color.getJavaVector3f(), brightness);
    }

    @JSCodingFunctionOrMethod(description = "Set the sun's brightness")
    public JSSunLight setSunBrightness(float brightness) {
        this.sunLight.setSunBrightness(brightness);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the sun's brightness")
    public float getSunBrightness() {
        return this.sunLight.getSunBrightness();
    }

    @JSCodingFunctionOrMethod(description = "Set the light's position")
    public JSSunLight setLightPosition(JSVector3f position) {
        this.sunLight.setLightPosition(position.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the light's position")
    public JSVector3f getLightPosition() {
        return new JSVector3f(this.sunLight.getLightPosition());
    }

    @JSCodingFunctionOrMethod(description = "Set the light's color")
    public JSSunLight setLightColor(JSVector3f color) {
        this.sunLight.setLightColor(color.getJavaVector3f());
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get the light's color")
    public JSVector3f getLightColor() {
        return new JSVector3f(this.sunLight.getLightColor());
    }

    @JSCodingFunctionOrMethod(description = "Enable the light")
    public JSSunLight on() {
        this.sunLight.on();
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Disable the light")
    public JSSunLight off() {
        this.sunLight.off();
        return this;
    }

    @Override
    public SunLight getJavaLight() {
        return this.sunLight;
    }
}