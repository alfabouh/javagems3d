package api.scripting.coding.env.internal.util.misc;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSString", description = "...")
public record JSString(@JSCodingFunctionOrMethod(description = "...") String string) {
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
    public String string() {
        return this.string;
    }

    @JSCodingConstructor(description = "...", paramNames = {"text"})
    public JSString {
    }
}