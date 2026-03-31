package api.scripting.coding.env.internal.util.lang;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import javagems3d.system.resources.localisation.LocalisationManager;

@JSCodingClass(binding = "JSLang", description = "Wrapper for localisation language.")
public class JSLang {

    private final LocalisationManager.Lang lang;

    public JSLang(LocalisationManager.Lang lang) {
        this.lang = lang;
    }

    @JSCodingFunctionOrMethod(description = "Get language name")
    public String getName() {
        return this.lang.lang();
    }

    public LocalisationManager.Lang getRaw() {
        return this.lang;
    }

    @Override
    public String toString() {
        return this.lang.lang();
    }
}