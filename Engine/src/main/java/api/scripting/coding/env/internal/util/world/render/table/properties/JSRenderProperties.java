package api.scripting.coding.env.internal.util.world.render.table.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.world.render.processing.JSCullingRules;
import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSRenderProperties", description = "Wrapper for JGemsRenderProperties, providing JS access to render properties.")
public class JSRenderProperties {

    @JSCodingField(description = "Underlying JGemsRenderProperties object")
    private final RenderProperties properties;

    @JSCodingConstructor(description = "Wraps an existing JGemsRenderProperties object", paramNames = {"properties"})
    public JSRenderProperties(@NotNull RenderProperties properties) {
        this.properties = properties;
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java JGemsRenderProperties object", paramNames = {})
    public @NotNull RenderProperties getJavaProperties() {
        return this.properties;
    }

    @JSCodingFunctionOrMethod(description = "Set a float property by key", paramNames = {"key", "value"})
    public JSRenderProperties setValueFloat(@NotNull String key, float value) {
        this.properties.setValueFloat(key, value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set a boolean property by key", paramNames = {"key", "value"})
    public JSRenderProperties setValueBool(@NotNull String key, boolean value) {
        this.properties.setValueBool(key, value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Set an integer property by key", paramNames = {"key", "value"})
    public JSRenderProperties setValueInt(@NotNull String key, int value) {
        this.properties.setValueInt(key, value);
        return this;
    }

    @JSCodingFunctionOrMethod(description = "Get a float property by key", paramNames = {"key"})
    public float getFloat(@NotNull String key) {
        return (float) this.properties.getFloat(key);
    }

    @JSCodingFunctionOrMethod(description = "Get a boolean property by key", paramNames = {"key"})
    public boolean getBool(@NotNull String key) {
        return this.properties.getBool(key);
    }

    @JSCodingFunctionOrMethod(description = "Get an integer property by key", paramNames = {"key"})
    public int getInt(@NotNull String key) {
        return this.properties.getInt(key);
    }

    @JSCodingFunctionOrMethod(description = "Get culling rules associated with these render properties", paramNames = {})
    public @NotNull JSCullingRules getCullingRules() {
        return new JSCullingRules(this.properties.getCullingRules());
    }

    @JSCodingFunctionOrMethod(description = "Creates a copy of this render properties wrapper", paramNames = {})
    public @NotNull JSRenderProperties copy() {
        return new JSRenderProperties(this.properties.copy());
    }

    @JSCodingFunctionOrMethod(description = "Returns a default instance of JSRenderProperties", paramNames = {})
    public static @NotNull JSRenderProperties getDefault() {
        return new JSRenderProperties(JGemsRenderProperties.getDefault());
    }
}