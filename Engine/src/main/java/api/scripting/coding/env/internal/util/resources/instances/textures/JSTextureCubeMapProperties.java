package api.scripting.coding.env.internal.util.resources.instances.textures;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSTextureCubeMapProperties", description = "Properties for cube map textures, e.g., linear filtration setting.")
public record JSTextureCubeMapProperties(boolean linearFiltration) {
    @JSCodingConstructor(description = "Constructor", paramNames = {"linearFiltration"})
    public JSTextureCubeMapProperties(boolean linearFiltration) {
        this.linearFiltration = linearFiltration;
    }

    @JSCodingConstructor(description = "DefaultPhysTest constructor")
    public JSTextureCubeMapProperties() {
        this(true);
    }

    @JSCodingFunctionOrMethod(description = "Check if linear filtration is enabled")
    @Override
    public boolean linearFiltration() {
        return this.linearFiltration;
    }

    @JSHideFromDoc
    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @JSHideFromDoc
    @Override
    public int hashCode() {
        return 0;
    }

    @JSHideFromDoc
    @Override
    public @NotNull String toString() {
        return "";
    }
}