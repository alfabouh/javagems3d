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

package api.scripting.coding.env.internal.game.init.events.resources;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.events.JSEventI;
import api.scripting.coding.env.internal.util.management.JSPath;
import api.scripting.coding.env.internal.util.resources.cache.JSSystemResources;
import api.scripting.coding.env.internal.util.resources.instances.shaders.JSShader;
import javagems3d.system.resources.assets.initialization.base.ShadersInitializer;
import javagems3d.system.resources.assets.shaders.manager.JGemsShaderManager;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;

@JSCodingClass(binding = "JSInitShadersEvent", description = "Event used to load shaders and register shader libraries.")
public class JSInitShadersEvent implements JSEventI {

    @JSCodingField(description = "Access to system resources required for shader creation.")
    public JSSystemResources systemResources;

    @JSHideFromDoc private ShadersInitializer<JGemsShaderManager> shadersInitializer;

    @JSCodingConstructor(description = "Internal event instance. Do not create manually.")
    public JSInitShadersEvent() {
    }

    @JSHideFromDoc
    public JSInitShadersEvent(ShadersInitializer<JGemsShaderManager> shadersInitializer, JSSystemResources systemResources) {
        this.systemResources = systemResources;
        this.shadersInitializer = shadersInitializer;
    }

    @JSCodingFunctionOrMethod(description = "Load and create shader from path.", paramNames = {"pathToShader"})
    public JSShader createShader(JSPath pathToShader) {
        return new JSShader(this.shadersInitializer.createShaderManager(this.systemResources.getJavaSystemResources().getResourceCache(), new JGemsPathSource(pathToShader.getJavaPath(), ISource.Source.OUTSIDE_JAR)));
    }

    @JSCodingFunctionOrMethod(description = "Register external shader library.", paramNames = {"pathToLibrary"})
    public void registerShaderLibrary(JSPath pathToLibrary) {
        this.shadersInitializer.getShaderLibrariesManager().createLibrary(new JGemsPathSource(pathToLibrary.getJavaPath(), ISource.Source.OUTSIDE_JAR));
    }

    @JSCodingFunctionOrMethod(description = "Register built-in shadow shader library.", paramNames = {})
    public void registerDefaultShaderLibrary_SHADOWS() {
        this.shadersInitializer.getShaderLibrariesManager().createLibrary(new JGemsPathSource("/assets/shaders/libs/shadows", ISource.Source.INSIDE_JAR));
    }

    @JSCodingFunctionOrMethod(description = "Register built-in animation shader library.", paramNames = {})
    public void registerDefaultShaderLibrary_ANIMATIONS() {
        this.shadersInitializer.getShaderLibrariesManager().createLibrary(new JGemsPathSource("/assets/shaders/libs/animations", ISource.Source.INSIDE_JAR));
    }

    @JSCodingFunctionOrMethod(description = "Register built-in lighting shader library.", paramNames = {})
    public void registerShaderLibrary_LIGHTING() {
        this.shadersInitializer.getShaderLibrariesManager().createLibrary(new JGemsPathSource("/assets/shaders/libs/lighting", ISource.Source.INSIDE_JAR));
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSInitShadersEvent";
    }
}