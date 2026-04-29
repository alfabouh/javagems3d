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