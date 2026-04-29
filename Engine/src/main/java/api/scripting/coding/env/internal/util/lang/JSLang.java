package api.scripting.coding.env.internal.util.lang;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.system.resources.localisation.LocalizationManager;

@JSCodingClass(binding = "JSLang", description = "Wrapper for localisation language.")
public class JSLang {

    private final LocalizationManager.Lang lang;

    public JSLang(LocalizationManager.Lang lang) {
        this.lang = lang;
    }

    @JSCodingFunctionOrMethod(description = "Get language name")
    public String getName() {
        return this.lang.lang();
    }

    public LocalizationManager.Lang getRaw() {
        return this.lang;
    }

    @Override
    public String toString() {
        return this.lang.lang();
    }
}