package api.scripting.coding.env.internal.util.resources.instances.textures;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSTexture2DProperties", description = "...")
public record JSTexture2DProperties(boolean mipMap, boolean linearFiltration, boolean shouldBeRepeated, boolean anisotropicFiltration, boolean qualityAffected) {
    @JSCodingConstructor(description = "Constructor", paramNames = {"mipMap", "linearFiltration", "shouldBeRepeated", "anisotropicFiltration", "qualityAffected"})
    public JSTexture2DProperties(boolean mipMap, boolean linearFiltration, boolean shouldBeRepeated, boolean anisotropicFiltration, boolean qualityAffected) {
        this.mipMap = mipMap;
        this.linearFiltration = linearFiltration;
        this.shouldBeRepeated = shouldBeRepeated;
        this.anisotropicFiltration = anisotropicFiltration;
        this.qualityAffected = qualityAffected;
    }

    @JSCodingConstructor(description = "Lite constructor", paramNames = {"mipMap", "qualityAffected"})
    public JSTexture2DProperties(boolean mipMap, boolean qualityAffected) {
        this(mipMap, true, true, true, qualityAffected);
    }

    public JSTexture2DProperties() {
        this(true, true, true, true, false);
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

    @JSCodingFunctionOrMethod(description = "...")
    @Override
    public boolean mipMap() {
        return this.mipMap;
    }

    @JSCodingFunctionOrMethod(description = "...")
    @Override
    public boolean linearFiltration() {
        return this.linearFiltration;
    }

    @JSCodingFunctionOrMethod(description = "...")
    @Override
    public boolean shouldBeRepeated() {
        return this.shouldBeRepeated;
    }

    @JSCodingFunctionOrMethod(description = "...")
    @Override
    public boolean anisotropicFiltration() {
        return this.anisotropicFiltration;
    }

    @JSCodingFunctionOrMethod(description = "...")
    @Override
    public boolean qualityAffected() {
        return this.qualityAffected;
    }
}