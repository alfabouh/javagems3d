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

@JSCodingClass(binding = "JSInitShadersEvent", description = "...")
public class JSInitShadersEvent implements JSEventI {
    @JSCodingField(description = "systemResources") public JSSystemResources systemResources;
    @JSHideFromDoc private ShadersInitializer<JGemsShaderManager> shadersInitializer;

    @JSCodingConstructor(description = "...")
    public JSInitShadersEvent() {
    }

    @JSHideFromDoc
    public JSInitShadersEvent(ShadersInitializer<JGemsShaderManager> shadersInitializer, JSSystemResources systemResources) {
        this.systemResources = systemResources;
        this.shadersInitializer = shadersInitializer;
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"pathToShader", "shadersInitializer"})
    public JSShader createShader(JSPath pathToShader) {
        return new JSShader(this.shadersInitializer.createShaderManager(this.systemResources.getJavaSystemResources().getResourceCache(), new JGemsPathSource(pathToShader.getJavaPath(), ISource.Source.OUTSIDE_JAR)));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"pathToLibrary", "shadersInitializer"})
    public void registerShaderLibrary(JSPath pathToLibrary) {
        this.shadersInitializer.getShaderLibrariesManager().createLibrary(new JGemsPathSource(pathToLibrary.getJavaPath(), ISource.Source.OUTSIDE_JAR));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"shadersInitializer"})
    public void registerDefaultShaderLibrary_SHADOWS() {
        this.shadersInitializer.getShaderLibrariesManager().createLibrary(new JGemsPathSource("/assets/jgems/shaders/libs/shadows", ISource.Source.INSIDE_JAR));
    }

    @JSCodingFunctionOrMethod(description = "...", paramNames = {"shadersInitializer"})
    public void registerDefaultShaderLibrary_ANIMATIONS() {
        this.shadersInitializer.getShaderLibrariesManager().createLibrary(new JGemsPathSource("/assets/jgems/shaders/libs/animations", ISource.Source.INSIDE_JAR));
    }
    @JSCodingFunctionOrMethod(description = "...", paramNames = {"shadersInitializer"})
    public void registerShaderLibrary_LIGHTING() {
        this.shadersInitializer.getShaderLibrariesManager().createLibrary(new JGemsPathSource("/assets/jgems/shaders/libs/lighting", ISource.Source.INSIDE_JAR));
    }

    @JSHideFromDoc
    @Override
    public String name() {
        return "JSInitShadersEvent";
    }
}
